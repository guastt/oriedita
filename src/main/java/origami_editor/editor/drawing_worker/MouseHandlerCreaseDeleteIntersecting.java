package origami_editor.editor.drawing_worker;

import origami.crease_pattern.element.LineColor;
import origami.crease_pattern.element.Point;
import origami_editor.editor.MouseMode;

public class MouseHandlerCreaseDeleteIntersecting extends BaseMouseHandler{
    public MouseHandlerCreaseDeleteIntersecting(DrawingWorker d) {
        super(d);
    }

    @Override
    public MouseMode getMouseMode() {
        return MouseMode.CREASE_DELETE_INTERSECTING_65;
    }

    //マウスを動かしたとき
    public void mouseMoved(Point p0) {
        d.mMoved_m_00b(p0, LineColor.MAGENTA_5);
    }//マウスで選択できる候補点を表示する。近くに既成の点があるときはその点、無いときはマウスの位置自身が候補点となる。

    //マウスクリック----------------------------------------------------
    public void mousePressed(Point p0) {
        d.mPressed_m_00b(p0, LineColor.MAGENTA_5);
    }

    //マウスドラッグ----------------------------------------------------
    public void mouseDragged(Point p0) {
        d.mDragged_m_00b(p0, LineColor.MAGENTA_5);
    }

//----------------------------------------------------------------------------------------
//多角形を入力(既存頂点への引き寄せあるが既存頂点が遠い場合は引き寄せ無し)し、何らかの作業を行うセット
    //マウス操作(マウスを動かしたとき)を行う関数

    //マウス操作(mouseMode==65　でボタンを離したとき)を行う関数----------------------------------------------------
    public void mouseReleased(Point p0) {

        d.i_drawing_stage = 0;
        Point p = new Point();
        p.set(d.camera.TV2object(p0));

        d.line_step[1].setA(p);
        d.closest_point.set(d.getClosestPoint(p));
        if (p.distance(d.closest_point) <= d.selectionDistance) {
            d.line_step[1].setA(d.closest_point);
        }
        if (d.line_step[1].getLength() > 0.00000001) {
            //やりたい動作はここに書く
            d.foldLineSet.deleteInsideLine(d.line_step[1], "lX");//lXは小文字のエルと大文字のエックス
            d.record();
        }
    }
}