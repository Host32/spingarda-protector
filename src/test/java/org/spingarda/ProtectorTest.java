package org.spingarda;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.mockito.*;
import org.spingarda.features.MathUtils;
import org.spingarda.features.Protector;
import org.spingarda.features.ProtectorParams;
import org.spingarda.features.Validator;
import org.springframework.social.facebook.api.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Created by Ivan on 20/11/2016.
 */
public class ProtectorTest {

    private Protector protector;

    private Facebook facebook;

    private CommentOperations commentOperations;

    private FeedOperations feedOperations;

    private ProtectorParams params;
    private String pageId;
    private Integer postsNumber;
    private Integer commentsNumber;
    private Integer usersNumber;

    private PagedList<Post> posts;
    private PagedList<Comment> comments;
    private List<String> userIds;

    @Given("^a pagina \"(.*?)\" estiver sob ataque$")
    public void a_pagina_estiver_sob_ataque(String pageId) throws Throwable {
        this.pageId = pageId;
    }

    @Given("^a pagina tiver \"(\\d+)\" postagens$")
    public void a_pagina_tiver_postagens(int postsNumber) throws Throwable {
        this.postsNumber = postsNumber;
    }

    @Given("^a pagina recebeu \"(\\d+)\" comentarios$")
    public void a_pagina_recebeu_comentarios_do_tipo_por_postagens(int commentsNumber) throws Throwable {
        this.commentsNumber = commentsNumber;
    }

    @Given("^os comentarios foram de \"(\\d+)\" usuarios diferentes$")
    public void os_comentarios_foram_de_usuarios_diferentes(Integer usersNumber) throws Throwable {
        this.usersNumber = usersNumber;
    }

    @When("^o administrador comecar a configurar o sistema$")
    public void o_administrador_comecar_a_configurar_o_sistema() throws Throwable {
        this.params = new ProtectorParams();
        this.params.setPostsLimit(100);
        this.params.setPostsOffset(0);
        this.params.setCommentsLimit(1000);
        this.params.setCommentsOffset(0);
    }

    @When("^a tolerancia de comentarios for \"(\\d+)\"$")
    public void a_tolerancia_de_comentarios_for(Integer arg1) throws Throwable {
        params.setCommentsNumber(arg1);
    }

    @When("^a tolerancia de comentarios for \"null\"$")
    public void a_tolerancia_de_comentarios_for_null() throws Throwable {
        params.setCommentsNumber(null);
    }

    @When("^a tolerancia de posts comentados for \"(\\d+)\"$")
    public void a_tolerancia_de_posts_comentados_for(int arg1) throws Throwable {
        params.setPostsCommentedNumber(arg1);
    }

    @When("^a tolerancia de posts comentados for \"null\"$")
    public void a_tolerancia_de_posts_comentados_for_null() throws Throwable {
        params.setPostsCommentedNumber(null);
    }

    @When("^a tolerancia de comentarios por posts for \"null\"$")
    public void a_tolerancia_de_comentarios_por_posts_for_null() throws Throwable {
        params.setCommentsByPost(null);
    }

    @When("^a tolerancia de comentarios por posts for \"(\\d+)\"$")
    public void a_tolerancia_de_comentarios_por_posts_for(Integer arg1) throws Throwable {
        params.setCommentsByPost(arg1);
    }

    @When("^a tolerancia de variacao do intervalo de publicacao for \"null\" milisegundos com um taxa de \"null\"% de diferenca$")
    public void a_tolerancia_de_variacao_do_intervalo_de_publicacao_for_null_milisegundos_com_um_taxa_de_null_de_diferenca() throws Throwable {
        params.setLowerCommentInterval(null);
        params.setLowerCommentIntervalRate(null);
    }

    @When("^a tolerancia de variacao do intervalo de publicacao for \"(\\d+)\" milisegundos com um taxa de \"(\\d+)\"% de diferenca$")
    public void a_tolerancia_de_variacao_do_intervalo_de_publicacao_for_milisegundos_com_um_taxa_de_de_diferenca(Long arg1, Double arg2) throws Throwable {
        params.setLowerCommentInterval(arg1);
        params.setLowerCommentIntervalRate(arg2);
    }

    @When("^a tolerancia do coeficiente de variacao de \"null\"$")
    public void a_tolerancia_do_coeficiente_de_variacao_de_null() throws Throwable {
        params.setCommentIntervalVariation(null);
    }

    @When("^a tolerancia do coeficiente de variacao de \"(\\d+)\"$")
    public void a_tolerancia_do_coeficiente_de_variacao_de(Double arg1) throws Throwable {
        params.setCommentIntervalVariation(arg1);
    }

    @When("^as palavras bloqueadas forem \"(.*?)\"$")
    public void as_palavras_bloqueadas_forem_puta_caralho(String arg1) throws Throwable {
        params.setBlockedWords(arg1);
    }

    @When("^os usuarios bloqueados forem \"(.*?)\"$")
    public void os_usuarios_bloqueados_forem(String arg1) throws Throwable {
        if (arg1.equals("null")) {
            arg1 = null;
        }
        params.setBannedUsers(arg1);
    }

    @When("^o admnistrador gostaria de \"(.*?)\" os comentarios$")
    public void o_admnistrador_gostaria_de_os_comentarios(String arg1) throws Throwable {
        if (arg1.equals("null")) {
            arg1 = null;
        }
        params.setHideOrDel(arg1);
    }

    private void generateUsers() {
        userIds = new ArrayList<>();

        for (int i = 0; i < usersNumber; i++) {
            userIds.add("user_" + i);
        }
    }

    private PagedList<Comment> generateComments(String postId) {
        List<Comment> comments = new ArrayList<>();

        generateUsers();

        for (int i = 0; i < commentsNumber; i++) {
            String commentId = postId + "_comment_" + i;

            Reference from = Mockito.mock(Reference.class);
            when(from.getId()).thenReturn(userIds.get((i + usersNumber) % usersNumber));

            Comment comment = Mockito.mock(Comment.class);
            when(comment.getId()).thenReturn(commentId);
            when(comment.getMessage()).thenReturn("Mensagem do comment " + i);
            when(comment.getFrom()).thenReturn(from);
            when(comment.getCreatedTime()).thenReturn(new Date());
            comments.add(comment);
        }

        return new PagedList<>(comments, null, null);
    }

    private PagedList<Post> generatePosts() {
        List<Post> posts = new ArrayList<>();

        for (int i = 0; i < postsNumber; i++) {
            String postId = "post_" + i;

            Post post = Mockito.mock(Post.class);
            when(post.getId()).thenReturn(postId);
            when(post.getMessage()).thenReturn("Mensagem do post " + i);
            posts.add(post);

            this.comments = generateComments(postId);

            when(commentOperations.getComments(eq(postId), any())).thenReturn(comments);
        }

        return new PagedList<>(posts, null, null);
    }

    @When("^o administrador terminar de configurar o sistema$")
    public void o_administrador_terminar_de_configurar_o_sistema() throws Throwable {

    }

    @When("^o administrador iniciar o protetor$")
    public void o_administrador_iniciar_o_protetor() throws Throwable {
        protector = new Protector();
        protector.setValidator(new Validator());
        protector.setMathUtils(new MathUtils());
        facebook = Mockito.mock(Facebook.class);
        commentOperations = Mockito.mock(CommentOperations.class);
        feedOperations = Mockito.mock(FeedOperations.class);

        when(facebook.feedOperations()).thenReturn(feedOperations);

        doAnswer(invocation -> {
            Thread.sleep(13);
            return null;
        }).when(facebook).post(any(), any());

        doAnswer(invocation -> {
            Thread.sleep(13);
            return null;
        }).when(commentOperations).deleteComment(any());

        this.posts = generatePosts();
        when(feedOperations.getFeed(eq(params.getPage()), any())).thenReturn(posts);

        protector.setFacebook(facebook);
        protector.setCommentOperations(commentOperations);
        protector.setUsers(new HashMap<>());
        protector.setParams(params);

        protector.setLog("");
        protector.setRunning(true);
        protector.effectiveRun();
        protector.setRunning(false);
        protector.setLog(protector.getLog() + "Done.\n");
    }

    @Then("^o log do sistema deve ter reportado \"(.*?)\" de \"(\\d+)\" comentarios$")
    public void o_log_do_sistema_deve_ter_reportado_de_comentarios(String logType, Integer expected) throws Throwable {
        String[] logLines = protector.getLog().split("\n");

        Integer found = 0;
        for (String logLine : logLines) {
            System.out.println(logLine);
            if (logLine.startsWith(logType)) {
                found++;
            }
        }

        assertThat(found).isEqualTo(expected);
    }

}
