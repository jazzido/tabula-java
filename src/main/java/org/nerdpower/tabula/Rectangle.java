package org.nerdpower.tabula;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.List;

@SuppressWarnings("serial")
public class Rectangle extends Rectangle2D.Float implements Comparable<Rectangle> {
    
    private static final float VERTICAL_COMPARISON_THRESHOLD = 0.4f;
        
    public Rectangle() {
        super();
    }
    
    public Rectangle(double top, double left, double width, double height) {
        super();
        this.setRect(left, top, width, height);
    }
    
    @Override
    public int compareTo(Rectangle other) {
        double thisBottom = this.getBottom();
        double otherBottom = other.getBottom();
        double yDifference = Math.abs(thisBottom - otherBottom);
        int rv;
//        if ((yDifference < VERTICAL_COMPARISON_THRESHOLD) ||
//                (otherBottom > this.getTop() && otherBottom < thisBottom) ||
//                (thisBottom > other.getTop() && thisBottom < otherBottom)) {
        if (this.verticalOverlap(other) > VERTICAL_COMPARISON_THRESHOLD) {
            rv = java.lang.Double.compare(this.getX(), other.getX());
        }
        else {
            rv = java.lang.Double.compare(thisBottom, otherBottom);
        }
        return rv;
    }
    
    public float getArea() {
        return this.width * this.height;
    }
    
    public float verticalOverlap(Rectangle other) {
        return (float) Math.max(0, Math.min(this.getBottom(), other.getBottom()) - Math.max(this.getTop(), other.getTop()));
    }
    
    public boolean verticallyOverlaps(Rectangle other) {
        return verticalOverlap(other) > 0;
    }
    
    public float verticalOverlapRatio(Rectangle other) {
        float o = verticalOverlap(other), rv = 0;
        if (o > 0) {
            rv = (float) (o / (Math.max(this.getBottom(), other.getBottom()) - Math.min(this.getTop(), other.getTop())));
        }
        return rv;
    }
    
    public boolean horizontallyOverlaps(Rectangle other) {
        return Math.max(0, Math.min(this.getRight(), other.getRight()) - Math.max(this.getLeft(), other.getLeft())) > 0;
    }
    
    public float horizontalOverlapRatio(Rectangle other) {
        float rv = 0, 
              delta = (float) Math.min(this.getBottom() - this.getTop(), other.getBottom() - other.getTop());
        
        if (other.getTop() <= this.getTop() && this.getTop() <= other.getBottom() && other.getBottom() <= this.getBottom()) { 
            rv = (float) ((other.getBottom() - this.getTop()) / delta);
        }
        else if (this.getTop() <= other.getTop() && other.getTop() <= this.getBottom() && this.getBottom() <= other.getBottom()) { 
            rv = (float) ((this.getBottom() - other.getTop()) / delta);
        }
        else if (this.getTop() <= other.getTop() && other.getTop() <= other.getBottom() && other.getBottom() <= this.getBottom()) {
            rv = (float) ((other.getBottom() - other.getTop()) / delta);
        }
        else if (other.getTop() <= this.getTop() && this.getTop() <= this.getBottom() && this.getBottom() <= other.getBottom()) {
            rv = (float) ((this.getBottom() - this.getTop()) / delta);
        }
        
        return rv;

    }
    
    public float overlapRatio(Rectangle other) {
        double intersectionWidth = Math.max(0, Math.min(this.getRight(), other.getRight()) - Math.max(this.getLeft(), other.getLeft()));
        double intersectionHeight = Math.max(0, Math.min(this.getBottom(), other.getBottom()) - Math.max(this.getTop(), other.getTop()));
        double intersectionArea = Math.max(0, intersectionWidth * intersectionHeight);
        double unionArea = this.getArea() + other.getArea() - intersectionArea;
        
        return (float) (intersectionArea / unionArea);
    }
    
    public Rectangle merge(Rectangle other) {
        this.setRect(this.createUnion(other));
        return this;
    }

    public double getTop() {
        return this.getMinY();
    }
    
    public void setTop(double top) {
        double deltaHeight = top - this.y;
        this.setRect(this.x, top, this.width, this.height - deltaHeight);
    }
    
    public double getRight() {
        return this.getMaxX();
    }
    
    public void setRight(double right) {
        this.setRect(this.x, this.y, right - this.x, this.height);
    }
        
    public double getLeft() {
        return this.getMinX();
    }
    
    public void setLeft(double left) {
        double deltaWidth = left - this.x;
        this.setRect(left, this.y, this.width - deltaWidth, this.height);
    }
    
    public double getBottom() {
        return this.getMaxY();
    }
    
    public void setBottom(double bottom) {
        this.setRect(this.x, this.y, this.width, bottom - this.y);
    }
    
    public Point2D[] getPoints() {
        return new Point2D[] {
                new Point2D.Float((float) this.getLeft(), (float) this.getTop()),
                new Point2D.Float((float) this.getRight(), (float) this.getTop()),
                new Point2D.Float((float) this.getRight(), (float) this.getBottom()),
                new Point2D.Float((float) this.getLeft(), (float) this.getBottom())
        };
    }
    
    public Ruling[] getLines(){
//    	+ # decomposes a rectangle into its 4 constitutent lines
//    	+ def to_lines
//    	+ # top left width height
//    	+ top = Line2D::Float.new self.left, self.top, self.right, self.top
//    	+ bottom = Line2D::Float.new self.left, self.bottom, self.right, self.bottom
//    	+ left = Line2D::Float.new self.left, self.top, self.left, self.bottom
//    	+ right = Line2D::Float.new self.right, self.top, self.right, self.bottom
//    	+ [top, bottom, left, right]
//    	+ end
    	Ruling top = new Ruling((float) getLeft(), (float) getTop(), (float) getRight(), (float) getTop());
    	Ruling bottom = new Ruling((float) getLeft(), (float) getBottom(), (float) getRight(), (float) getBottom());
    	Ruling left = new Ruling((float) getLeft(), (float) getTop(), (float) getLeft(), (float) getBottom());
    	Ruling right = new Ruling((float) getRight(), (float) getTop(), (float) getRight(), (float) getBottom());
    	if(top.isFinite() && bottom.isFinite() && left.isFinite() && right.isFinite()){
        	return new Ruling[]{ top, bottom, left, right };
    	}else{
    		return new Ruling[]{};
    	}
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        String s = super.toString();
        sb.append(s.substring(0, s.length() - 1));
        sb.append(String.format(",bottom=%f,right=%f]", this.getBottom(), this.getRight()));
        return sb.toString();
    }

    
    /**
     * @param rectangles
     * @return minimum bounding box that contains all the rectangles
     */
    public static Rectangle boundingBoxOf(List<? extends Rectangle> rectangles) {
        float minx = java.lang.Float.MAX_VALUE;
        float miny = java.lang.Float.MAX_VALUE;
        float maxx = java.lang.Float.MIN_VALUE;
        float maxy = java.lang.Float.MIN_VALUE;
        
        for (Rectangle r: rectangles) {
            minx = (float) Math.min(r.getMinX(), minx);
            miny = (float) Math.min(r.getMinY(), miny);
            maxx = (float) Math.max(r.getMaxX(), maxx);
            maxy = (float) Math.max(r.getMaxY(), maxy);
        }
        return new Rectangle(miny, minx, maxx - minx, maxy - miny);
    }
    
    
}
