package julianpraesent.gradeplanner.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Builder
@EqualsAndHashCode
@AllArgsConstructor
public class Course {

    private String id;

    private String title;

    private TypeEnum type;

    private int ects;

    private GradeEnum grade;

    private boolean graded;

    private boolean locked;

    private boolean modified;
}
