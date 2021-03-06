package seedu.address.logic.commands;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;

import org.junit.Test;

import seedu.address.logic.CommandHistory;
import seedu.address.mocks.TypicalModelManagerStub;
import seedu.address.model.Model;
import seedu.address.ui.ReaderViewStyle;
import seedu.address.ui.ViewMode;
import seedu.address.ui.ViewType;

/**
 * Contains integration tests (interaction with the Model) for {@code ViewModeCommand}.
 */
public class ViewModeCommandTest {
    private Model model = new TypicalModelManagerStub();
    private Model expectedModel = new TypicalModelManagerStub();
    private CommandHistory commandHistory = new CommandHistory();

    @Test
    public void execute_viewMode_success() {
        // test view types success
        for (ViewType viewType: ViewType.values()) {
            assertExecutionSuccess(new ViewMode(viewType));
        }

        // test reader view styles success
        for (ReaderViewStyle viewStyle: ReaderViewStyle.values()) {
            assertExecutionSuccess(new ViewMode(ViewType.READER, viewStyle));
        }
    }

    @Test
    public void equals() {
        ViewModeCommand viewModeFirstCommand = new ViewModeCommand(new ViewMode(ViewType.BROWSER));
        ViewModeCommand viewModeSecondCommand = new ViewModeCommand(new ViewMode(ViewType.READER));

        // same object -> returns true
        assertEquals(viewModeFirstCommand, viewModeFirstCommand);

        // same values -> returns true
        ViewModeCommand viewModeFirstCommandCopy = new ViewModeCommand(new ViewMode(ViewType.BROWSER));
        assertEquals(viewModeFirstCommand, viewModeFirstCommandCopy);

        // different types -> returns false
        assertNotEquals(viewModeFirstCommand, 1);

        // null -> returns false
        assertNotEquals(viewModeFirstCommand, null);

        // different entry -> returns false
        assertNotEquals(viewModeFirstCommand, viewModeSecondCommand);
    }

    /**
     * Executes a {@code ViewModeCommand} with the given {@code viewMode},
     * and checks that the model's view mode is set to the specified {@code viewMode}.
     */
    private void assertExecutionSuccess(ViewMode viewMode) {
        ViewModeCommand viewModeCommand = new ViewModeCommand(viewMode);
        String expectedMessage = String.format(ViewModeCommand.MESSAGE_SET_VIEW_MODE_SUCCESS, viewMode.toString());
        expectedModel.setViewMode(viewMode);

        assertCommandSuccess(viewModeCommand, model, commandHistory, expectedMessage, expectedModel);
    }

    /* code snippet for future use
    /**
     * Executes a {@code ViewModeCommand} with the given {@code viewMode}, and checks that a {@code CommandException}
     * is thrown with the {@code expectedMessage}.
     */
    /*
    private void assertExecutionFailure(ViewMode viewMode, String expectedMessage) {
        ViewModeCommand viewModeCommand = new ViewModeCommand(viewMode);
        assertCommandFailure(viewModeCommand, model, commandHistory, expectedMessage);
    }
    */
}
