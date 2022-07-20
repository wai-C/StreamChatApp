package com.example.streamchat

import android.app.Application
import io.getstream.chat.android.client.ChatClient
import io.getstream.chat.android.client.logger.ChatLogLevel
import io.getstream.chat.android.offline.model.message.attachments.UploadAttachmentsNetworkType
import io.getstream.chat.android.offline.plugin.configuration.Config
import io.getstream.chat.android.offline.plugin.factory.StreamOfflinePluginFactory


class StreamApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        val offlinePluginFactory = StreamOfflinePluginFactory(
            config = Config(
                // Enables the background sync which is performed to sync user actions done without the Internet connection.
                backgroundSyncEnabled = true,
                // Enables the ability to receive information about user activity such as last active date and if they are online right now.
                userPresence = true,
                // Enables using the database as an internal caching mechanism.
                persistenceEnabled = true,
                // An enumeration of various network types used as a constraint inside upload attachments worker.
                uploadAttachmentsNetworkType = UploadAttachmentsNetworkType.NOT_ROAMING,
                // Whether the SDK will use a new sequential event handling mechanism.
                useSequentialEventHandler = false,
            ),
            appContext = this,
        )
        //log level to show all log info of this stream chat
        val client = ChatClient.Builder(getString(R.string.api_key),this).logLevel(ChatLogLevel.ALL).withPlugin(offlinePluginFactory).build()
    }
}