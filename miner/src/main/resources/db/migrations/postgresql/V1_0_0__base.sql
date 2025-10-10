CREATE TABLE IF NOT EXISTS Channel
(
    ID               VARCHAR(32)  NOT NULL PRIMARY KEY,
    Username         VARCHAR(128) NOT NULL,
    LastStatusChange TIMESTAMP(3) NOT NULL,
    CONSTRAINT UsernameIdx UNIQUE (Username)
);

CREATE INDEX IF NOT EXISTS Channel_UsernameIdx ON Channel (Username);

CREATE TABLE IF NOT EXISTS Balance
(
    ID          SERIAL PRIMARY KEY,
    ChannelID   VARCHAR(32)  NOT NULL,
    BalanceDate TIMESTAMP(3) NOT NULL,
    Balance     INTEGER      NOT NULL,
    Reason      VARCHAR(16),
    CONSTRAINT Balance_Channel_FK FOREIGN KEY (ChannelID) REFERENCES Channel (ID) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS Balance_BalanceDateIdx ON Balance (BalanceDate);

CREATE TABLE IF NOT EXISTS Prediction
(
    ID          SERIAL PRIMARY KEY,
    ChannelID   VARCHAR(32) NOT NULL,
    EventID     VARCHAR(36) NOT NULL,
    EventDate   TIMESTAMP   NOT NULL,
    Type        VARCHAR(16),
    Description VARCHAR(255),
    CONSTRAINT Prediction_Channel_FK FOREIGN KEY (ChannelID) REFERENCES Channel (ID) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS Prediction_EventDateIdx ON Prediction (EventDate);
CREATE INDEX IF NOT EXISTS Prediction_EventTypeIdx ON Prediction (Type);

CREATE TABLE IF NOT EXISTS ResolvedPrediction
(
    EventID           VARCHAR(36) NOT NULL PRIMARY KEY,
    ChannelID         VARCHAR(32) NOT NULL,
    Title             VARCHAR(64) NOT NULL,
    EventCreated      TIMESTAMP   NOT NULL,
    EventEnded        TIMESTAMP,
    Canceled          BOOLEAN     NOT NULL,
    Outcome           VARCHAR(32),
    Badge             VARCHAR(32),
    ReturnRatioForWin DOUBLE PRECISION,
    CONSTRAINT ResolvedPrediction_Channel_FK FOREIGN KEY (ChannelID) REFERENCES Channel (ID) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS ResolvedPrediction_ChannelIDIdx ON ResolvedPrediction (ChannelID);

CREATE TABLE IF NOT EXISTS PredictionUser
(
    ID                 SERIAL PRIMARY KEY,
    Username           VARCHAR(128)     NOT NULL,
    ChannelID          VARCHAR(32)      NOT NULL,
    PredictionCnt      SMALLINT         NOT NULL DEFAULT 0,
    WinCnt             SMALLINT         NOT NULL DEFAULT 0,
    WinRate            NUMERIC(8, 7)    NOT NULL DEFAULT 0,
    ReturnOnInvestment DOUBLE PRECISION NOT NULL DEFAULT 0,
    CONSTRAINT PredictionUser_Unique_User_Channel UNIQUE (Username, ChannelID),
    CONSTRAINT PredictionUser_Channel_FK FOREIGN KEY (ChannelID) REFERENCES Channel (ID) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS PredictionUser_UsernameIdx ON PredictionUser (Username, ChannelID);

CREATE TABLE IF NOT EXISTS UserPrediction
(
    UserID    INTEGER     NOT NULL,
    ChannelID VARCHAR(32) NOT NULL,
    Badge     VARCHAR(32) NOT NULL,
    PRIMARY KEY (ChannelID, UserID),
    CONSTRAINT UserPrediction_User_FK FOREIGN KEY (UserID) REFERENCES PredictionUser (ID) ON DELETE CASCADE,
    CONSTRAINT UserPrediction_Channel_FK FOREIGN KEY (ChannelID) REFERENCES Channel (ID) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS UserPrediction_ChannelIDIdx ON UserPrediction (ChannelID);
CREATE INDEX IF NOT EXISTS UserPrediction_UserIDIdx ON UserPrediction (UserID);