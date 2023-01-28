CREATE TABLE IF NOT EXISTS `Channel`
(
    `ID`               VARCHAR(32)  NOT NULL PRIMARY KEY,
    `Username`         VARCHAR(128) NOT NULL,
    `LastStatusChange` DATETIME     NOT NULL,
    INDEX `UsernameIdx` (`Username`)
)
    ENGINE = InnoDB
    CHARACTER SET 'utf8mb4'
    COLLATE 'utf8mb4_general_ci';

CREATE TABLE IF NOT EXISTS `Balance`
(
    `ID`          INT         NOT NULL PRIMARY KEY AUTO_INCREMENT,
    `ChannelID`   VARCHAR(32) NOT NULL REFERENCES `Channel` (`ID`),
    `BalanceDate` DATETIME(3) NOT NULL,
    `Balance`     INT         NOT NULL,
    `Reason`      VARCHAR(16) NULL,
    INDEX `PointsDateIdx` (`BalanceDate`)
)
    ENGINE = InnoDB
    CHARACTER SET 'utf8mb4'
    COLLATE 'utf8mb4_general_ci';

CREATE TABLE IF NOT EXISTS `Prediction`
(
    `ID`          INT          NOT NULL PRIMARY KEY AUTO_INCREMENT,
    `ChannelID`   VARCHAR(32)  NOT NULL REFERENCES `Channel` (`ID`),
    `EventID`     VARCHAR(36)  NOT NULL,
    `EventDate`   DATETIME     NOT NULL,
    `Type`        VARCHAR(16)  NULL,
    `Description` VARCHAR(255) NULL,
    INDEX `EventDateIdx` (`EventDate`),
    INDEX `EventTypeIdx` (`Type`)
)
    ENGINE = InnoDB
    CHARACTER SET 'utf8mb4'
    COLLATE 'utf8mb4_general_ci';

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
    `ReturnRatioForWin` DOUBLE      NULL,
    INDEX `ChannelIDIdx` (`ChannelID`)
)
    ENGINE = InnoDB
    CHARACTER SET 'utf8mb4'
    COLLATE 'utf8mb4_general_ci';

CREATE TABLE IF NOT EXISTS `PredictionUser`
(
    `ID`                 INT               NOT NULL PRIMARY KEY AUTO_INCREMENT,
    `Username`           VARCHAR(128)      NOT NULL,
    `ChannelID`          VARCHAR(32)       NOT NULL REFERENCES `Channel` (`ID`),
    `PredictionCnt`      SMALLINT UNSIGNED NOT NULL DEFAULT 0,
    `WinCnt`             SMALLINT UNSIGNED NOT NULL DEFAULT 0,
    `WinRate`            DECIMAL(8, 7)     NOT NULL DEFAULT 0,
    `ReturnOnInvestment` DOUBLE            NOT NULL DEFAULT 0,
    UNIQUE (`Username`, `ChannelID`),
    INDEX `UsernameIdx` (`Username`, `ChannelID`)
)
    ENGINE = InnoDB
    CHARACTER SET 'utf8mb4'
    COLLATE 'utf8mb4_general_ci';

CREATE TABLE IF NOT EXISTS `UserPrediction`
(
    `UserID`    INT         NOT NULL REFERENCES `PredictionUser` (`ID`),
    `ChannelID` VARCHAR(32) NOT NULL REFERENCES `Channel` (`ID`),
    `Badge`     VARCHAR(32) NOT NULL,
    PRIMARY KEY (`ChannelID`, `UserID`),
    INDEX `ChannelIDIdx` (`ChannelID`),
    INDEX `UserIDIdx` (`UserID`)
)
    ENGINE = InnoDB
    CHARACTER SET 'utf8mb4'
    COLLATE 'utf8mb4_general_ci';
