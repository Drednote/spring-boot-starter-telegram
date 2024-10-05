package io.github.drednote.telegram.session;

import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.telegram.telegrambots.meta.api.objects.Update;

@Getter
@Setter
public class UpdateResponse {

    private boolean ok;
    private List<Update> result;
}
