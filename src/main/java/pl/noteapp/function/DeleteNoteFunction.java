package pl.noteapp.function;

import com.microsoft.azure.functions.*;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DeleteNoteFunction {

    @FunctionName("deleteNote")
    public HttpResponseMessage deleteNote(
            @HttpTrigger(
                    name = "req",
                    methods = {HttpMethod.DELETE},
                    authLevel = AuthorizationLevel.ANONYMOUS)
                    HttpRequestMessage<Integer> request,
            final ExecutionContext context) {
        int noteId = request.getBody();
        try {
            final String connectionUrl = "jdbc:sqlserver://noteapp.database.windows.net:1433;database=noteapp;user=noteappAdmin@noteapp;password=adminNoteapp!;encrypt=true;trustServerCertificate=false;hostNameInCertificate=*.database.windows.net;loginTimeout=30";
            DriverManager.registerDriver(new com.microsoft.sqlserver.jdbc.SQLServerDriver());
            Connection connection = DriverManager.getConnection(connectionUrl);
            String updateSql = "delete from [dbo].[note] where note_id = ?";
            PreparedStatement statement = connection.prepareStatement(updateSql);
            statement.setObject(1, noteId);
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST).body("Please pass a body").build();
        }
        return request.createResponseBuilder(HttpStatus.ACCEPTED)
                .header("Content-Type", "application/json")
                .body("Note deleted successfully")
                .build();
    }
}
