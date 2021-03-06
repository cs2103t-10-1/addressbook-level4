package seedu.address;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.logging.Logger;

import javafx.application.Application;
import javafx.stage.Stage;
import seedu.address.commons.core.Config;
import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.core.Version;
import seedu.address.commons.exceptions.DataConversionException;
import seedu.address.commons.util.ConfigUtil;
import seedu.address.commons.util.StringUtil;
import seedu.address.logic.Logic;
import seedu.address.logic.LogicManager;
import seedu.address.model.EntryBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.ReadOnlyEntryBook;
import seedu.address.model.ReadOnlyUserPrefs;
import seedu.address.model.UserPrefs;
import seedu.address.storage.ArticleStorage;
import seedu.address.storage.DataConversionAndIoExceptionsThrowingSupplier;
import seedu.address.storage.DataDirectoryArticleStorage;
import seedu.address.storage.EntryBookStorage;
import seedu.address.storage.JsonEntryBookStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.Storage;
import seedu.address.storage.StorageManager;
import seedu.address.storage.UserPrefsStorage;
import seedu.address.ui.Ui;
import seedu.address.ui.UiManager;
import seedu.address.util.Network;

/**
 * The main entry point to the application.
 */
public class MainApp extends Application {

    public static final Version VERSION = new Version(1, 3, 1, true);

    private static final Logger logger = LogsCenter.getLogger(MainApp.class);

    protected Ui ui;
    protected Logic logic;
    protected Storage storage;
    protected Model model;
    protected Config config;

    @Override
    public void init() throws Exception {
        logger.info("=============================[ Initializing README ]===========================");
        super.init();

        AppParameters appParameters = AppParameters.parse(getParameters());
        config = initConfig(appParameters.getConfigPath());

        UserPrefsStorage userPrefsStorage = new JsonUserPrefsStorage(config.getUserPrefsFilePath());
        UserPrefs userPrefs = initPrefs(userPrefsStorage);
        EntryBookStorage listEntryBookStorage = new JsonEntryBookStorage(userPrefs.getListEntryBookFilePath());
        EntryBookStorage archivesEntryBookStorage = new JsonEntryBookStorage(userPrefs.getArchivesEntryBookFilePath());
        EntryBookStorage feedsEntryBookStorage = new JsonEntryBookStorage(userPrefs.getFeedsEntryBookFilePath());
        ArticleStorage articleStorage = new DataDirectoryArticleStorage(userPrefs.getArticleDataDirectoryPath());

        storage = new StorageManager(listEntryBookStorage, archivesEntryBookStorage, feedsEntryBookStorage,
                userPrefsStorage, articleStorage);

        initLogging(config);

        model = initModelManager(storage, userPrefs);

        logic = new LogicManager(model);

        ui = new UiManager(logic);
    }

    /**
     * Returns a {@code ModelManager} with the data from {@code storage}'s address book and {@code userPrefs}. <br>
     * The data from the sample address book will be used instead if {@code storage}'s address book is not found,
     * or an empty address book will be used instead if errors occur when reading {@code storage}'s address book.
     */
    private Model initModelManager(Storage storage, ReadOnlyUserPrefs userPrefs) {

        ReadOnlyEntryBook initialListEntryBook = initEntryBook(storage::readListEntryBook, EntryBook::new,
                "reading list");
        ReadOnlyEntryBook initialArchivesEntryBook = initEntryBook(storage::readArchivesEntryBook, EntryBook::new,
                "archives");
        ReadOnlyEntryBook initialFeedEntryBook = initEntryBook(storage::readFeedsEntryBook, EntryBook::new,
                "feed list");

        return new ModelManager(initialListEntryBook, initialArchivesEntryBook, initialFeedEntryBook, userPrefs,
                storage);
    }


    /**
     * Returns an initialized EntryBook given a method reference which reads it from storage and another function
     * which returns a sample EntryBook.
     * Also takes in the name of the EntryBook initialized for logging messages.
     */
    private ReadOnlyEntryBook initEntryBook(
            DataConversionAndIoExceptionsThrowingSupplier<Optional<ReadOnlyEntryBook>> storageFetcher,
            Supplier<ReadOnlyEntryBook> sampleEntryBookSupplier,
            String entryBookName) {

        try {
            Optional<ReadOnlyEntryBook> fetchedEntryBook = storageFetcher.get();
            if (!fetchedEntryBook.isPresent()) {
                logger.info("Data file not found. Will be starting with a default " + entryBookName);
            }
            return fetchedEntryBook.orElseGet(sampleEntryBookSupplier);
        } catch (DataConversionException e) {
            logger.warning("Data file not in the correct format. Will be starting with an empty " + entryBookName);
            return new EntryBook();
        } catch (IOException e) {
            logger.warning("Problem while reading from the file. Will be starting with an empty " + entryBookName);
            return new EntryBook();
        }
    }

    private void initLogging(Config config) {
        LogsCenter.init(config);
    }

    /**
     * Returns a {@code Config} using the file at {@code configFilePath}. <br>
     * The default file path {@code Config#DEFAULT_CONFIG_FILE} will be used instead
     * if {@code configFilePath} is null.
     */
    protected Config initConfig(Path configFilePath) {
        Config initializedConfig;
        Path configFilePathUsed;

        configFilePathUsed = Config.DEFAULT_CONFIG_FILE;

        if (configFilePath != null) {
            logger.info("Custom Config file specified " + configFilePath);
            configFilePathUsed = configFilePath;
        }

        logger.info("Using config file : " + configFilePathUsed);

        try {
            Optional<Config> configOptional = ConfigUtil.readConfig(configFilePathUsed);
            initializedConfig = configOptional.orElse(new Config());
        } catch (DataConversionException e) {
            logger.warning("Config file at " + configFilePathUsed + " is not in the correct format. "
                    + "Using default config properties");
            initializedConfig = new Config();
        }

        //Update config file in case it was missing to begin with or there are new/unused fields
        try {
            ConfigUtil.saveConfig(initializedConfig, configFilePathUsed);
        } catch (IOException e) {
            logger.warning("Failed to save config file : " + StringUtil.getDetails(e));
        }
        return initializedConfig;
    }

    /**
     * Returns a {@code UserPrefs} using the file at {@code storage}'s user prefs file path,
     * or a new {@code UserPrefs} with default configuration if errors occur when
     * reading from the file.
     */
    protected UserPrefs initPrefs(UserPrefsStorage storage) {
        Path prefsFilePath = storage.getUserPrefsFilePath();
        logger.info("Using prefs file : " + prefsFilePath);

        UserPrefs initializedPrefs;
        try {
            Optional<UserPrefs> prefsOptional = storage.readUserPrefs();
            initializedPrefs = prefsOptional.orElse(new UserPrefs());
        } catch (DataConversionException e) {
            logger.warning("UserPrefs file at " + prefsFilePath + " is not in the correct format. "
                    + "Using default user prefs");
            initializedPrefs = new UserPrefs();
        } catch (IOException e) {
            logger.warning("Problem while reading from the file. Will be starting with an empty EntryBook");
            initializedPrefs = new UserPrefs();
        }

        //Update prefs file in case it was missing to begin with or there are new/unused fields
        try {
            storage.saveUserPrefs(initializedPrefs);
        } catch (IOException e) {
            logger.warning("Failed to save config file : " + StringUtil.getDetails(e));
        }

        return initializedPrefs;
    }

    @Override
    public void start(Stage primaryStage) {
        logger.info("Starting README " + MainApp.VERSION);
        ui.start(primaryStage);
    }

    @Override
    public void stop() {
        logger.info("============================ [ Stopping README ] =============================");
        try {
            Network.stop();
        } catch (IOException e) {
            logger.severe("Failed to terminate remaining network connections " + StringUtil.getDetails(e));
        }
        try {
            storage.saveUserPrefs(model.getUserPrefs());
        } catch (IOException e) {
            logger.severe("Failed to save preferences " + StringUtil.getDetails(e));
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
