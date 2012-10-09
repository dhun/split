package jp.gr.java_conf.dhun.split.manager;

import jp.gr.java_conf.dhun.split.persistence.entity.GameRecord;

public interface IGameListener {
    void onUpdateHighScore(GameConfig config, GameRecord gameRecord);
}
