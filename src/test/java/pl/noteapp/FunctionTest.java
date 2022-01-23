package pl.noteapp;

import com.microsoft.azure.functions.*;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.sql.SQLException;
import java.util.*;
import java.util.logging.Logger;

import org.junit.jupiter.api.Test;
import pl.noteapp.function.*;
import pl.noteapp.model.Note;
import pl.noteapp.modelDTO.NoteDTO;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class FunctionTest {

    @Test
    public void testHttpTriggerJava() throws SQLException {

        final HttpRequestMessage<String> req = mock(HttpRequestMessage.class);

        final HttpRequestMessage<NoteDTO> reqNote = mock(HttpRequestMessage.class);

        final HttpRequestMessage<Integer> reqNoteInteger = mock(HttpRequestMessage.class);

        final Map<String, String> queryParams = new HashMap<>();
        queryParams.put("name", "Azure");
        doReturn(queryParams).when(req).getQueryParameters();

        final Optional<String> queryBody = Optional.empty();
        doReturn(queryBody).when(req).getBody();

        doAnswer(new Answer<HttpResponseMessage.Builder>() {
            @Override
            public HttpResponseMessage.Builder answer(InvocationOnMock invocation) {
                HttpStatus status = (HttpStatus) invocation.getArguments()[0];
                return new HttpResponseMessageMock.HttpResponseMessageBuilderMock().status(status);
            }
        }).when(req).createResponseBuilder(any(HttpStatus.class));

        final ExecutionContext context = mock(ExecutionContext.class);
        doReturn(Logger.getGlobal()).when(context).getLogger();

        // Invoke
        final HttpResponseMessage addNote = new AddOneNoteFunction().addNote(reqNote, context);
        final HttpResponseMessage editNote = new EditNoteFunction().editNote(reqNote, context);
        final HttpResponseMessage deleteNote = new DeleteNoteFunction().deleteNote(reqNoteInteger, context);
        final HttpResponseMessage oneNote = new OneNoteFunction().getOneNote(req, context);
        final HttpResponseMessage noteList = new NoteListFunction().getNoteList(req, context);

        // Verify
        assertEquals(addNote.getStatus(), HttpStatus.OK);
        assertEquals(editNote.getStatus(), HttpStatus.OK);
        assertEquals(deleteNote.getStatus(), HttpStatus.OK);
        assertEquals(oneNote.getStatus(), HttpStatus.OK);
        assertEquals(noteList.getStatus(), HttpStatus.OK);
    }

}