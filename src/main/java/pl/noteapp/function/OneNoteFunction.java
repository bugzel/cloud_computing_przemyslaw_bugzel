package pl.noteapp.function;

import com.microsoft.azure.functions.*;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;
import pl.noteapp.model.Note;

import java.sql.*;

public class OneNoteFunction {

        @FunctionName("oneNote")
    public HttpResponseMessage getOneNote(

            @HttpTrigger(
                    name = "req",
                    methods = {HttpMethod.GET},
                    authLevel = AuthorizationLevel.ANONYMOUS)
                    HttpRequestMessage<String> request,
            final ExecutionContext context) throws SQLException {
        int noteId = Integer.parseInt(request.getQueryParameters().get("name"));
            ResultSet rs = null;
        Note note = new Note();
        try {
            final String connectionUrl = "jdbc:sqlserver://noteapp.database.windows.net:1433;database=noteapp;user=noteappAdmin@noteapp;password=adminNoteapp!;encrypt=true;trustServerCertificate=false;hostNameInCertificate=*.database.windows.net;loginTimeout=30";
            DriverManager.registerDriver(new com.microsoft.sqlserver.jdbc.SQLServerDriver());
            Connection connection = DriverManager.getConnection(connectionUrl);
            String selectSql = "select * from [dbo].[note] where note_id = ?";
            PreparedStatement statement = connection.prepareStatement(selectSql);
            statement.setObject(1, noteId);
            rs = statement.executeQuery();
            if(rs.next()){
                note.setNote(rs.getString("note"));
                note.setNoteDate(rs.getString("note_date"));
                note.setNoteId(noteId);
            }
            return request.createResponseBuilder(HttpStatus.OK)
                    .header("Content-Type", "application/json")
                    .body(note)
                    .build();
        } catch (SQLException e) {
            e.printStackTrace();
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST).body("Please pass a body").build();
        }
    }
}
