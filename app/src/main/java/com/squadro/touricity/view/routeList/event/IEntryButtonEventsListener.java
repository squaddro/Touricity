package com.squadro.touricity.view.routeList.event;

import com.squadro.touricity.message.types.AbstractEntry;

public interface IEntryButtonEventsListener {
    enum EDirection {
        UP,
        DOWN
    }
    void onMoveEntry(AbstractEntry entry, EDirection direction);
    void onRemoveEntry(AbstractEntry entry);
    void onClickEntry(AbstractEntry entry);
    void onHoldEntry(AbstractEntry entry);
    void onEditEntry(AbstractEntry entry);
}
