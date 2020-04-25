package ooga.custom;

import java.awt.geom.Point2D;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MoveNodeOr extends MoveNode implements Serializable {

    public MoveNodeOr(List<MoveNode> children) {
        // TODO: Throw an error if 'children' has less than two nodes.
        super(children);
    }

    public MoveNodeOr(MoveNode a, MoveNode b) {
        super(List.of(a, b));
    }

    @Override
    public MoveNode invokeConstructor(List<MoveNode> children) {
        return new MoveNodeOr(children);
    }

    @Override
    public List<Point2D> generatePoints() {
        List<Point2D> pointList = new ArrayList<>();
        for (MoveNode subNode : this.getChildren()) {
            pointList.addAll(subNode.generatePoints());
        }
        return pointList;
    }
}
