package codesquad.domain;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Embeddable
public class DeleteHistories {

    @OneToMany(fetch=FetchType.EAGER, cascade = CascadeType.ALL)
    List<DeleteHistory> histories = new ArrayList<>();

    public DeleteHistories addHistory(DeleteHistory history) {
        histories.add(history);
        return this;
    }

    public List<DeleteHistory> toList() {
        return histories;
    }

    public int size() {
        return histories.size();
    }
}
