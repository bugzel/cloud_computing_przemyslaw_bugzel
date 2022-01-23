package pl.noteapp.function;

import com.microsoft.azure.functions.*;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;
import org.modelmapper.ModelMapper;
import pl.noteapp.model.Note;
import pl.noteapp.modelDTO.NoteDTO;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;

public class AddOneNoteFunction {

    private final ModelMapper modelMapper = new ModelMapper();

    @FunctionName("addNote")
    public HttpResponseMessage addNote(
            @HttpTrigger(
                    name = "req",
                    methods = {HttpMethod.POST},
                    authLevel = AuthorizationLevel.ANONYMOUS)
                    HttpRequestMessage<NoteDTO> request,
            final ExecutionContext context) {
        NoteDTO noteDTO = request.getBody();
        Note note = modelMapper.map(noteDTO, Note.class);
        try {
            final String connectionUrl = "jdbc:sqlserver://noteapp.database.windows.net:1433;database=noteapp;user=noteappAdmin@noteapp;password=adminNoteapp!;encrypt=true;trustServerCertificate=false;hostNameInCertificate=*.database.windows.net;loginTimeout=30";
            DriverManager.registerDriver(new com.microsoft.sqlserver.jdbc.SQLServerDriver());
            Connection connection = DriverManager.getConnection(connectionUrl);
            String selectSql = "insert into [dbo].[note] (note, note_date) values (?, ?)";
            PreparedStatement statement = connection.prepareStatement(selectSql);
            statement.setObject(1, note.getNote());
            statement.setObject(2, LocalDateTime.now().toString());
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST).body("Please pass a body").build();
        }
        return request.createResponseBuilder(HttpStatus.OK)
                .header("Content-Type", "application/json")
                .body("Note created successfully")
                .build();
    }
}
