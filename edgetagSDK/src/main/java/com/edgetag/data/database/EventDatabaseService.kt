package com.edgetag.data.database

import com.edgetag.DependencyInjectorImpl
import com.edgetag.repository.EventRepository

class EventDatabaseService {

    private var evenDao = DependencyInjectorImpl.getInstance().getEventDatabase().eventDao()
    private var eventRepository: EventRepository = DependencyInjectorImpl.getEventRepository()

    companion object {
        private const val TAG = "EventDatabaseService"
    }
}
