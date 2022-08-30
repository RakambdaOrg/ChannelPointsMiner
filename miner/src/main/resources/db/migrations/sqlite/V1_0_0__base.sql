CREATE TABLE IF NOT EXISTS `Channel`
(
    `ID`               VARCHAR(32)  NOT NULL PRIMARY KEY,
    `Username`         VARCHAR(128) NOT NULL,
    `LastStatusChange` DATETIME     NOT NULL
);

CREATE TABLE IF NOT EXISTS `Balance`
(
    `ID`          INTEGER     NOT NULL PRIMARY KEY AUTOINCREMENT,
    `ChannelID`   VARCHAR(32) NOT NULL REFERENCES `Channel` (`ID`),
    `BalanceDate` DATETIME(3) NOT NULL,
    `Balance`     INTEGER     NOT NULL,
    `Reason`      VARCHAR(16) NULL
);

CREATE INDEX IF NOT EXISTS `PointsDateIdx` ON `Balance` (`BalanceDate`);

CREATE TABLE IF NOT EXISTS `Prediction`
(
    `ID`          INTEGER      NOT NULL PRIMARY KEY AUTOINCREMENT,
    `ChannelID`   VARCHAR(32)  NOT NULL REFERENCES `Channel` (`ID`),
    `EventID`     VARCHAR(36)  NOT NULL,
    `EventDate`   DATETIME     NOT NULL,
    `Type`        VARCHAR(16)  NULL,
    `Description` VARCHAR(255) NULL
);

CREATE INDEX IF NOT EXISTS `EventDateIdx` ON `Prediction` (`EventDate`);

CREATE INDEX IF NOT EXISTS `EventTypeIdx` ON `Prediction` (`Type`);

CREATE TABLE IF NOT EXISTS `ResolvedPrediction`
(
    `EventID`           VARCHAR(36) NOT NULL PRIMARY KEY,
    `ChannelID`         VARCHAR(32) NOT NULL REFERENCES `Channel` (`ID`),
    `Title`             VARCHAR(64) NOT NULL,
    `EventCreated`      DATETIME    NOT NULL,
    `EventEnded`        DATETIME    NULL,
    `Canceled`          BOOLEAN     NOT NULL,
    `Outcome`           VARCHAR(32) NULL,
    `Badge`             VARCHAR(32) NULL,
    `ReturnRatioForWin` REAL        NULL
);

CREATE INDEX IF NOT EXISTS `ChannelIDIdx` ON `ResolvedPrediction` (`ChannelID`);

CREATE TABLE IF NOT EXISTS `PredictionUser`
(
    `ID`                 INTEGER           NOT NULL PRIMARY KEY AUTOINCREMENT,
    `Username`           VARCHAR(128)      NOT NULL,
    `ChannelID`          VARCHAR(32)       NOT NULL REFERENCES `Channel` (`ID`),
    `PredictionCnt`      SMALLINT UNSIGNED NOT NULL DEFAULT 0,
    `WinCnt`             SMALLINT UNSIGNED NOT NULL DEFAULT 0,
    `WinRate`            REAL              NOT NULL DEFAULT 0,
    `ReturnOnInvestment` REAL              NOT NULL DEFAULT 0,
    UNIQUE (`Username`, `ChannelID`)
);

CREATE INDEX IF NOT EXISTS `UsernameIdx` ON `PredictionUser` (`Username`);

CREATE TABLE IF NOT EXISTS `UserPrediction`
(
    `UserID`    INTEGER     NOT NULL REFERENCES `PredictionUser` (`ID`),
    `ChannelID` VARCHAR(32) NOT NULL REFERENCES `Channel` (`ID`),
    `Badge`     VARCHAR(32) NOT NULL,
    PRIMARY KEY (`ChannelID`, `UserID`)
);

CREATE INDEX IF NOT EXISTS `ChannelIDIdx` ON `UserPrediction` (`ChannelID`);

CREATE INDEX IF NOT EXISTS `UserIDIdx` ON `UserPrediction` (`UserID`);