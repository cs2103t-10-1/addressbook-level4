package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import seedu.address.commons.core.Messages;
import seedu.address.commons.util.CollectionUtil;
import seedu.address.logic.CommandHistory;
import seedu.address.model.Model;
import seedu.address.model.entry.EntryContainsSearchTermsPredicate;
import seedu.address.model.tag.Tag;

/**
 * Finds and lists all entries in entry book whose title contains any of the argument keywords.
 * Keyword matching is case insensitive.
 */
public class FindCommand extends Command {

    public static final String COMMAND_WORD = "find";
    public static final String COMMAND_ALIAS = "f";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Finds all entries whose title contain any of "
            + "the specified keywords (case-insensitive) and displays them as a list with index numbers.\n"
            + "Parameters: KEYWORD [MORE_KEYWORDS]...\n"
            + "Example: " + COMMAND_WORD + " trump amazon";
    public static final String MESSAGE_NO_SEARCH_TERMS = "At least one field to edit must be provided.";

    private final EntryContainsSearchTermsPredicate predicate;

    public FindCommand(EntryContainsSearchTermsPredicate predicate) {
        this.predicate = predicate;
    }

    @Override
    public CommandResult execute(Model model, CommandHistory history) {
        requireNonNull(model);
        model.updateFilteredEntryList(predicate);
        return new CommandResult(
                String.format(Messages.MESSAGE_ENTRIES_LISTED_OVERVIEW, model.getFilteredEntryList().size()));
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof FindCommand // instanceof handles nulls
                && predicate.equals(((FindCommand) other).predicate)); // state check
    }

    /**
     * Stores the details of a search command for an entry. Each non-empty field value will be used to
     * search for an existing entry using the corresponding field.
     */
    public static class FindEntryDescriptor {
        private String title;
        private String description;
        private String link;
        private String address;
        private Set<Tag> tags;

        public FindEntryDescriptor() {}

        /**
         * Copy constructor.
         * A defensive copy of {@code tags} is used internally.
         */
        public FindEntryDescriptor(FindEntryDescriptor toCopy) {
            setTitle(toCopy.title);
            setDescription(toCopy.description);
            setLink(toCopy.link);
            setAddress(toCopy.address);
            setTags(toCopy.tags);
        }

        /**
         * Returns true if at least one field is edited.
         */
        public boolean isAnyFieldEdited() {
            return CollectionUtil.isAnyNonNull(title, link, description, address, tags);
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public Optional<String> getTitle() {
            return Optional.ofNullable(title);
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public Optional<String> getDescription() {
            return Optional.ofNullable(description);
        }

        public void setLink(String link) {
            this.link = link;
        }

        public Optional<String> getLink() {
            return Optional.ofNullable(link);
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public Optional<String> getAddress() {
            return Optional.ofNullable(address);
        }

        /**
         * Sets {@code tags} to this object's {@code tags}.
         * A defensive copy of {@code tags} is used internally.
         */
        public void setTags(Set<Tag> tags) {
            this.tags = (tags != null) ? new HashSet<>(tags) : null;
        }

        /**
         * Returns an unmodifiable tag set, which throws {@code UnsupportedOperationException}
         * if modification is attempted.
         * Returns {@code Optional#empty()} if {@code tags} is null.
         */
        public Optional<Set<Tag>> getTags() {
            return (tags != null) ? Optional.of(Collections.unmodifiableSet(tags)) : Optional.empty();
        }

        @Override
        public boolean equals(Object other) {
            // short circuit if same object
            if (other == this) {
                return true;
            }

            // instanceof handles nulls
            if (!(other instanceof FindEntryDescriptor)) {
                return false;
            }

            // state check
            FindEntryDescriptor e = (FindEntryDescriptor) other;

            return getTitle().equals(e.getTitle())
                && getDescription().equals(e.getDescription())
                && getLink().equals(e.getLink())
                && getAddress().equals(e.getAddress())
                && getTags().equals(e.getTags());
        }
    }
}
