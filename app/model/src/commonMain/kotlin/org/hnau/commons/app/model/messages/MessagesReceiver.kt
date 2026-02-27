package org.hnau.commons.app.model.messages

interface MessagesReceiver<T> {

    fun sendMessage(message: T)
}
