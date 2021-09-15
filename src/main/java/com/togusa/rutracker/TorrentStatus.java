package com.togusa.rutracker;

public enum TorrentStatus {

    APPROVED("проверено"),
    NOT_APPROVED("не проверено"),
    NEED_EDIT("недооформлено"),
    DUBIOUSLY("сомнительно"),
    CONSUMED("поглощено"),
    TEMPORARY("временная"),
    CLOSED("закрыто");

    private final String status;

    String getStatus() {
        return this.status;
    }

    TorrentStatus(String status) {
        this.status = status;
    }

    static TorrentStatus valueOfLabel(String label) {
        for (TorrentStatus e : values()) {
            if (e.status.equals(label)) {
                return e;
            }
        }
        return null;
    }
}
