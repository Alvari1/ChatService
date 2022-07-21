class ChatService {
    private var chatId = 0L
    private var messageId = 0L
    private var chats: List<Chat> = emptyList()

    fun createMessage(ownerId: UserId, content: String, chatId: Long = -1L): Message {
        val message = Message(MessageId(messageId++), ownerId, content)

        // create chat
        if (chats.none { it.id == chatId }) {
            chats = chats + Chat(this.chatId++, listOf(message))
            return message
        }

        //update chat
        chats = chats.map { chat ->
            if (chat.id == chatId) chat.copy(messages = chat.messages + message)
            else chat
        }
        return message
    }

    fun editMessage(messageId: MessageId, content: String) {
        chats = chats.map { chat ->
            chat.copy(
                messages = chat.messages.map { message ->
                    if (message.id == messageId) message.copy(content = content)
                    else message
                }
            )
        }
    }

    fun removeMessage(messageId: MessageId) {
        chats = chats.map { chat ->
            chat.copy(messages = chat.messages.filter { it.id != messageId })
        }
    }

    fun readMessage(chatId: Long, userId: UserId, messageId: MessageId) {
        val index = chats.indexOfFirst { it.id == chatId }
            .takeIf { it >= 0 } ?: return

        val chat = chats[index]
        val newReadMessages = chat.readMessages.toMutableMap()
            .apply {
                put(userId, messageId)
            }
        chats = chats.toMutableList()
            .apply {
                set(index, chat.copy(readMessages = newReadMessages))
            }
    }

    fun getMessages(chatId: Long): List<Message> =
        chats.filter {
            it.id == chatId
        }.map {
            it.messages
        }.flatten()

    fun removeChat(chatId: Long) {
        chats = chats.filter { it.id != chatId }
    }

    fun getUnreadCount(chatId: Long, userId: UserId): Int {
        val chatIndex = chats.indexOfFirst { it.id == chatId }
            .takeIf { it >= 0 } ?: return 0

        val chat = chats[chatIndex]
        val readMessageId = chat.readMessages[userId]
        val readMessages = chat.messages.takeWhile {
            it.id != readMessageId
        }
        return readMessages.filterNot {
            it.ownerId == userId
        }.size
    }
}