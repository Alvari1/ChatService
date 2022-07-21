import org.junit.Test

import org.junit.Assert.*

class ChatServiceTest {
    private val chatService = ChatService()

    @Test
    fun `create new message`() {
        val ownerId = UserId(0)
        val chatId = 0L

        val content = "test 1"
        val message = Message(MessageId(0), ownerId, content)
        val expectedResult = listOf(message)

        chatService.createMessage(ownerId, content)
        val result = chatService.getMessages(chatId)

        assertEquals(expectedResult, result)
    }

    @Test
    fun `get unread messages count from empty chat`() {
        val userId = UserId(0)
        val chatId = 0L
        val expectedResult = 0

        val result = chatService.getUnreadCount(chatId, userId)

        assertEquals(expectedResult, result)
    }

    @Test
    fun `get unread messages count from filled chats`() {
        val ownerId1 = UserId(0)
        val ownerId2 = UserId(1)
        val chatId = 0L
        val content = "test 1"

        val readMessageId = MessageId(10)
        val expectedResult = 4

        chatService.createMessage(ownerId1, content)
        repeat(10) {
            chatService.createMessage(ownerId1, content, chatId)
            chatService.createMessage(ownerId2, content, chatId)
        }
        chatService.readMessage(chatId, ownerId1, readMessageId)
        val result = chatService.getUnreadCount(chatId, ownerId1)

        assertEquals(expectedResult, result)
    }
}