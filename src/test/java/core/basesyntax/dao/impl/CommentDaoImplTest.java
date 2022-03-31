package core.basesyntax.dao.impl;

import java.util.List;
import core.basesyntax.AbstractTest;
import core.basesyntax.dao.CommentDao;
import core.basesyntax.dao.SmileDao;
import core.basesyntax.model.Comment;
import core.basesyntax.model.Smile;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class CommentDaoImplTest extends AbstractTest {
    private CommentDao commentDao;

    @Before
    public void before() {
        commentDao = new CommentDaoImpl(getSessionFactory());
    }

    @Test
    public void create_NoSmiles_Ok() {
        Comment perfectComment = new Comment();
        perfectComment.setContent("This article is awesome!!!");
        Comment actual = commentDao.create(perfectComment);
        Assert.assertNotNull("Check you have implemented the `create` method " +
                "in the CommentDaoImpl class", actual);
        Assert.assertNotNull("ID for comment should be autogenerated", actual.getId());
        Assert.assertEquals(1L, actual.getId().longValue());
    }

    @Test
    public void getCommentById_NoSmiles_Ok() {
        // Create a new comment entity
        Comment awesomeComment = new Comment();
        awesomeComment.setContent("This article is awesome!!!");
        commentDao.create(awesomeComment);
        Assert.assertNotNull("ID for comment should be autogenerated", awesomeComment.getId());
        Assert.assertEquals(1L, awesomeComment.getId().longValue());

        // Get comment by ID
        Comment actual = commentDao.get(1L);
        Assert.assertNotNull(actual);
        Assert.assertNotNull(actual.getId());
        Assert.assertNotNull(actual.getContent());
        Assert.assertEquals(1L, actual.getId().longValue());
        Assert.assertEquals("This article is awesome!!!", actual.getContent());
    }

    @Test
    public void createAndReadAll_NoSmiles_Ok() {
        // Create a new comment entity
        Comment awesomeComment = new Comment();
        awesomeComment.setContent("This article is awesome!!!");
        commentDao.create(awesomeComment);
        Assert.assertNotNull("ID for comment should be autogenerated", awesomeComment.getId());
        Assert.assertEquals(1L, awesomeComment.getId().longValue());

        // Validate the result entities list
        List<Comment> allComments = commentDao.getAll();
        Assert.assertNotNull(allComments);
        Assert.assertFalse(allComments.isEmpty());
        Assert.assertEquals(1, allComments.size());
        Assert.assertNotNull(allComments.get(0));
        Assert.assertNotNull(allComments.get(0).getId());
        Assert.assertEquals(1L, allComments.get(0).getId().longValue());
    }

    @Test
    public void createAndReadAll_TwoEntities_NoSmiles_Ok() {
        // Create a first comment entity
        Comment awesomeComment = new Comment();
        awesomeComment.setContent("This article is awesome!!!");
        commentDao.create(awesomeComment);
        Assert.assertNotNull("ID for comment should be autogenerated", awesomeComment.getId());
        Assert.assertEquals(1L, awesomeComment.getId().longValue());

        // Create a second comment entity
        Comment notBadComment = new Comment();
        notBadComment.setContent("This article is not bad");
        commentDao.create(notBadComment);
        Assert.assertNotNull("ID for comment should be autogenerated", notBadComment.getId());
        Assert.assertEquals(2L, notBadComment.getId().longValue());

        // Validate the result entities list
        List<Comment> allComments = commentDao.getAll();
        Assert.assertNotNull(allComments);
        Assert.assertFalse(allComments.isEmpty());
        Assert.assertEquals(2, allComments.size());

        // Validate first entity
        Assert.assertNotNull(allComments.get(0));
        Assert.assertNotNull(allComments.get(0).getId());
        Assert.assertNotNull(allComments.get(0).getContent());
        Assert.assertEquals(1L, allComments.get(0).getId().longValue());
        Assert.assertEquals("This article is awesome!!!", allComments.get(0).getContent());

        // Validate second entity
        Assert.assertNotNull(allComments.get(1));
        Assert.assertNotNull(allComments.get(1).getId());
        Assert.assertNotNull(allComments.get(1).getContent());
        Assert.assertEquals(2L, allComments.get(1).getId().longValue());
        Assert.assertEquals("This article is not bad", allComments.get(1).getContent());
    }

    @Test
    public void addComment_WithNotExistedSmiles_Exception() {
        // Create a new comment entity
        Comment awesomeComment = new Comment();
        awesomeComment.setContent("This article is awesome!!!");
        Smile funnySmile = new Smile("funny");
        Smile awesomeSmile = new Smile("awesome");
        awesomeComment.setSmiles(List.of(funnySmile, awesomeSmile));
        try {
            commentDao.create(awesomeComment);
        } catch (RuntimeException e) {
            return;
        }
        Assert.fail("You should not create a smiles when saving a comment. " +
                "Use only smiles stored in DB. Check your cascade setting");
    }

    @Test
    public void addComment_WithExistedSmiles_Ok() {
        SmileDao smileDao = new SmileDaoImpl(getSessionFactory());

        // Create smiles and store in DB
        Smile funnySmile = new Smile("funny");
        Smile awesomeSmile = new Smile("awesome");
        smileDao.create(funnySmile);
        smileDao.create(awesomeSmile);
        Assert.assertEquals(1L, funnySmile.getId().longValue());
        Assert.assertEquals(2L, awesomeSmile.getId().longValue());

        // Create a new comment entity
        Comment perfectComment = new Comment();
        perfectComment.setContent("This article is awesome!!!");
        perfectComment.setSmiles(List.of(funnySmile, awesomeSmile));
        commentDao.create(perfectComment);
        Assert.assertNotNull("ID for comment should be autogenerated", perfectComment.getId());
        Assert.assertEquals(1L, perfectComment.getId().longValue());

        // Validate smiles
        Assert.assertNotNull(perfectComment.getSmiles());
        Assert.assertFalse(perfectComment.getSmiles().isEmpty());
        Assert.assertEquals(2, perfectComment.getSmiles().size());
        Assert.assertEquals(1L, perfectComment.getSmiles().get(0).getId().longValue());
        Assert.assertEquals(2L, perfectComment.getSmiles().get(1).getId().longValue());

        // Validate comments are ok
        Assert.assertEquals(1L, perfectComment.getSmiles().get(0).getId().longValue());
        Assert.assertEquals("funny", perfectComment.getSmiles().get(0).getValue());
        Assert.assertEquals(2L, perfectComment.getSmiles().get(1).getId().longValue());
    }

    @Test
    public void removeComment_NoSmile_Ok() {
        SmileDao smileDao = new SmileDaoImpl(getSessionFactory());

        // Create smiles and store in DB
        Smile funnySmile = new Smile("funny");
        Smile awesomeSmile = new Smile("awesome");
        smileDao.create(funnySmile);
        smileDao.create(awesomeSmile);
        Assert.assertEquals(1L, funnySmile.getId().longValue());
        Assert.assertEquals(2L, awesomeSmile.getId().longValue());

        // Validate funny smile is in the DB
        Smile actualFunny = smileDao.get(1L);
        Assert.assertNotNull(actualFunny);
        Assert.assertNotNull(actualFunny.getId());
        Assert.assertNotNull(actualFunny.getValue());
        Assert.assertEquals(1L, actualFunny.getId().longValue());
        Assert.assertEquals("funny", actualFunny.getValue());

        // Validate awesome smile is in the DB
        Smile actualAwesome = smileDao.get(2L);
        Assert.assertNotNull(actualAwesome);
        Assert.assertNotNull(actualAwesome.getId());
        Assert.assertNotNull(actualAwesome.getValue());
        Assert.assertEquals(2L, actualAwesome.getId().longValue());
        Assert.assertEquals("awesome", actualAwesome.getValue());

        // Create a new comment entity
        Comment awesomeComment = new Comment();
        awesomeComment.setContent("This article is awesome!!!");
        awesomeComment.setSmiles(List.of(funnySmile, awesomeSmile));
        commentDao.create(awesomeComment);
        Assert.assertNotNull("ID for comment should be autogenerated", awesomeComment.getId());
        Assert.assertEquals(1L, awesomeComment.getId().longValue());

        // Validate the result entities list
        List<Comment> allComments = commentDao.getAll();
        Assert.assertNotNull(allComments);
        Assert.assertFalse(allComments.isEmpty());
        Assert.assertEquals(1, allComments.size());
        Assert.assertNotNull(allComments.get(0));
        Assert.assertNotNull(allComments.get(0).getId());
        Assert.assertNotNull(allComments.get(0).getContent());
        Assert.assertEquals(1L, allComments.get(0).getId().longValue());
        Assert.assertEquals("This article is awesome!!!", allComments.get(0).getContent());

        // Remove the comment
        commentDao.remove(awesomeComment);

        // Validate not comments are in the DB
        List<Comment> actualAfterRemoving = commentDao.getAll();
        Assert.assertNotNull(actualAfterRemoving);
        Assert.assertTrue(actualAfterRemoving.isEmpty());

        // Validate smiles are still in the DB

        // Validate funny smile is in the DB after removing
        Smile actualFunnyAfterCommentRemove = smileDao.get(1L);
        Assert.assertNotNull(actualFunnyAfterCommentRemove);
        Assert.assertNotNull(actualFunnyAfterCommentRemove.getId());
        Assert.assertNotNull(actualFunnyAfterCommentRemove.getValue());
        Assert.assertEquals(1L, actualFunnyAfterCommentRemove.getId().longValue());
        Assert.assertEquals("funny", actualFunnyAfterCommentRemove.getValue());

        // Validate awesome smile is in the DB after removing
        Smile actualAwesomeAfterCommentRemove = smileDao.get(2L);
        Assert.assertNotNull(actualAwesomeAfterCommentRemove);
        Assert.assertNotNull(actualAwesomeAfterCommentRemove.getId());
        Assert.assertNotNull(actualAwesomeAfterCommentRemove.getValue());
        Assert.assertEquals(2L, actualAwesomeAfterCommentRemove.getId().longValue());
        Assert.assertEquals("awesome", actualAwesomeAfterCommentRemove.getValue());

    }

    @Test
    public void removeComment_WithTwoSmiles_Ok() {
        // Create a new comment entity
        Comment awesomeComment = new Comment();
        awesomeComment.setContent("This article is awesome!!!");
        commentDao.create(awesomeComment);
        Assert.assertNotNull("ID for comment should be autogenerated", awesomeComment.getId());
        Assert.assertEquals(1L, awesomeComment.getId().longValue());

        // Validate the result entities list
        List<Comment> allComments = commentDao.getAll();
        Assert.assertNotNull(allComments);
        Assert.assertFalse(allComments.isEmpty());
        Assert.assertEquals(1, allComments.size());
        Assert.assertNotNull(allComments.get(0));
        Assert.assertNotNull(allComments.get(0).getId());
        Assert.assertNotNull(allComments.get(0).getContent());
        Assert.assertEquals(1L, allComments.get(0).getId().longValue());
        Assert.assertEquals("This article is awesome!!!", allComments.get(0).getContent());

        // Remove the comment
        commentDao.remove(awesomeComment);

        // Validate not comments are in the DB
        List<Comment> actualAfterRemoving = commentDao.getAll();
        Assert.assertNotNull(actualAfterRemoving);
        Assert.assertTrue(actualAfterRemoving.isEmpty());
    }

    @Override
    protected Class<?>[] entities() {
        return new Class[]{Comment.class, Smile.class};
    }
}
