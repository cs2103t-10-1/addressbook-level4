package seedu.address.logic.commands;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.logic.commands.FeedCommand.MESSAGE_FAILURE_NET;
import static seedu.address.logic.commands.FeedCommand.MESSAGE_FAILURE_XML;
import static seedu.address.logic.commands.FeedCommand.MESSAGE_SUCCESS;

import java.net.MalformedURLException;
import java.net.URL;

import org.junit.Test;

import seedu.address.MainApp;
import seedu.address.commons.util.FeedUtil;
import seedu.address.logic.CommandHistory;
import seedu.address.mocks.ModelManagerStub;
import seedu.address.model.EntryBook;
import seedu.address.model.Model;
import seedu.address.model.ModelContext;
import seedu.address.testutil.TestUtil;

public class FeedCommandTest {
    private static final URL TEST_URL =
        TestUtil.toUrl("https://cs2103-ay1819s2-w10-1.github.io/main/networktests/rss.xml");
    private static final URL TEST_URL_LOCAL =
        MainApp.class.getResource("/RssFeedTest/rss.xml");
    private static final URL TEST_BAD_FEED_URL =
        MainApp.class.getResource("/RssFeedTest/badrss.xml");
    private static final URL NOTAFEED_URL =
        TestUtil.toUrl("https://cs2103-ay1819s2-w10-1.github.io/main/networktests/notafeed.notxml");
    private static final URL NOTAWEBSITE_URL =
        TestUtil.toUrl("https://this.website.does.not.exist.definitely/");

    private Model model = new ModelManagerStub();
    private CommandHistory commandHistory = new CommandHistory();

    /** Asserts that executing a FeedCommand with the given url imports the Entry list. */
    public void assertFeedSuccessfullyLoaded(URL feedUrl) throws Exception {
        Model model = new ModelManagerStub();
        Model expectedModel = new ModelManagerStub();
        CommandHistory commandHistory = new CommandHistory();

        String expectedMessage = String.format(MESSAGE_SUCCESS, feedUrl);
        FeedCommand command = new FeedCommand(feedUrl);
        EntryBook expectedDisplayedEntryBook = FeedUtil.fromFeedUrl(feedUrl);

        expectedModel.setSearchEntryBook(expectedDisplayedEntryBook);
        expectedModel.setContext(ModelContext.CONTEXT_SEARCH);

        assertCommandSuccess(command, model, commandHistory, expectedMessage, expectedModel);
    }

    @Test
    public void equals() throws MalformedURLException {
        URL firstUrl = new URL("https://open.kattis.com/rss/new-problems");
        URL secondUrl = new URL("https://en.wikipedia.org/w/index.php?title=Special:RecentChanges&feed=rss");

        FeedCommand feedFirstCommand = new FeedCommand(firstUrl);
        FeedCommand feedSecondCommand = new FeedCommand(secondUrl);

        // same object -> returns true
        assertTrue(feedFirstCommand.equals(feedFirstCommand));

        // same values -> returns true
        FeedCommand feedFirstCommandCopy = new FeedCommand(firstUrl);
        assertTrue(feedFirstCommand.equals(feedFirstCommandCopy));

        // different types -> returns false
        assertFalse(feedFirstCommand.equals(1));

        // null -> returns false
        assertFalse(feedFirstCommand.equals(null));

        // different entry -> returns false
        assertFalse(feedFirstCommand.equals(feedSecondCommand));
    }

    @Test
    public void execute_localUrl_success() throws Exception {
        assertFeedSuccessfullyLoaded(TEST_URL_LOCAL);
    }

    @Test
    public void execute_badFeed_success() throws Exception {
        assertFeedSuccessfullyLoaded(TEST_BAD_FEED_URL);
    }

    @Test
    public void execute_remoteUrl_success() throws Exception {
        assertFeedSuccessfullyLoaded(TEST_URL);
    }

    @Test
    public void execute_urlIsNotAFeed_commandFails() {
        String expectedMessage = String.format(MESSAGE_FAILURE_XML, NOTAFEED_URL);
        FeedCommand command = new FeedCommand(NOTAFEED_URL);

        assertCommandFailure(command, model, commandHistory, expectedMessage);
    }

    @Test
    public void execute_urlIsNotAWebsite_commandFails() {
        String expectedMessage = String.format(MESSAGE_FAILURE_NET,
            "java.net.UnknownHostException: this.website.does.not.exist.definitely");
        FeedCommand command = new FeedCommand(NOTAWEBSITE_URL);

        assertCommandFailure(command, model, commandHistory, expectedMessage);
    }
}