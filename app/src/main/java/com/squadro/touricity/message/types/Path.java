package com.squadro.touricity.message.types;

import com.squadro.touricity.message.types.interfaces.IPath;

import java.util.List;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder(toBuilder = true)
public class Path extends Entry implements IPath {

    private String path_id;
    private String path_type;
    private List<Vertex> vertices;

    public Path(String entry_id, float expense, float duration, String comment, String path_id,
                String path_type, List<Vertex> vertices) {
        super(entry_id, expense, duration, comment);
        this.path_id = path_id;
        this.path_type = path_type;
        this.vertices = vertices;
    }
}
