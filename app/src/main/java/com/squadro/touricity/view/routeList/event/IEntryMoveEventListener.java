package com.squadro.touricity.view.routeList.event;

import com.squadro.touricity.message.types.AbstractEntry;

public interface IEntryMoveEventListener {
    enum EDirection {
        UP,
        DOWN
    }
    void onMoveEntry(AbstractEntry entry, EDirection direction);
}
