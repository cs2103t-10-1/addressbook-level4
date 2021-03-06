package seedu.address.model;

import seedu.address.logic.parser.EntryBookArchivesParser;
import seedu.address.logic.parser.EntryBookFeedsParser;
import seedu.address.logic.parser.EntryBookListParser;
import seedu.address.logic.parser.EntryBookParser;
import seedu.address.logic.parser.EntryBookSearchParser;

/**
 * Enums for contexts Model can take
 */
public enum ModelContext {
    CONTEXT_LIST(new EntryBookListParser(), "Reading List"),
    CONTEXT_ARCHIVES(new EntryBookArchivesParser(), "Archives"),
    CONTEXT_SEARCH(new EntryBookSearchParser(), "Results"),
    CONTEXT_FEEDS(new EntryBookFeedsParser(), "Feeds");

    private EntryBookParser parser;
    private String friendlyName;

    ModelContext(EntryBookParser parser, String friendlyName) {
        this.parser = parser;
        this.friendlyName = friendlyName;
    }

    public EntryBookParser getParser() {
        return parser;
    }

    @Override
    public String toString() {
        return friendlyName;
    }

}
