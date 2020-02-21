package com.squadro.touricity.message.types;

import com.squadro.touricity.message.types.interfaces.IPath;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Path extends AbstractEntry implements IPath {

    public enum PathType{
        WALKING(0),
        BUS(1),
        DRIVING(2);

        int value;
        PathType(int i){
            value = i;
        }

        public int getValue(){
            return value;
        }
    }

    private String path_id;
    private PathType path_type;
    private List<PathVertex> vertices;
    private String type = "path";

    public Path(String entry_id, int expense, int duration, String comment, String path_id,
                PathType path_type, List<PathVertex> vertices) {
        super(entry_id, expense, duration, comment);
        this.path_id = path_id;
        this.path_type = path_type;
        this.vertices = vertices;
    }

    @Override
    public String getType() {
        return "path";
    }
}
