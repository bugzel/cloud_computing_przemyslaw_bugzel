package pl.noteapp.function;

import com.microsoft.azure.functions.*;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;
import pl.noteapp.model.Note;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NoteListFunction {

    @FunctionName("noteList")
    public HttpResponseMessage getNoteList(
            @HttpTrigger(
                    name = "req",
                    methods = {HttpMethod.GET},
                    authLevel = AuthorizationLevel.ANONYMOUS)
                    HttpRequestMessage<String> request,
            final ExecutionContext context) {
        final String query = request.getQueryParameters().get("name");
        ResultSet rs = null;
        List<Note> noteList = new ArrayList<>();
        try {
            final String connectionUrl = "jdbc:sqlserver://noteapp.database.windows.net:1433;database=noteapp;user=noteappAdmin@noteapp;password=adminNoteapp!;encrypt=true;trustServerCertificate=false;hostNameInCertificate=*.database.windows.net;loginTimeout=30";
            DriverManager.registerDriver(new com.microsoft.sqlserver.jdbc.SQLServerDriver());
            Connection connection = DriverManager.getConnection(connectionUrl);
            String selectSql = "select * from [dbo].[note]";
            PreparedStatement statement = connection.prepareStatement(selectSql);
            rs = statement.executeQuery();
            while(rs.next()){
                Note note = new Note();
                note.setNoteId(rs.getInt("note_id"));
                note.setNote(rs.getString("note"));
                note.setNoteDate(rs.getString("note_date"));
                noteList.add(note);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST).body("Please pass a body").build();
        }
        return request.createResponseBuilder(HttpStatus.OK)
                .header("Content-Type", "application/json")
                .body(noteList)
                .build();
    }
}
