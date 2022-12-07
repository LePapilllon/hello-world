package test;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
public class Test_Arrow {
    //绘制面板
    public static class DrawPanel extends JPanel {

        private BasicStroke lineStroke;//stroke属性控制线条的宽度、笔形样式、线段连接方式或短划线图案 属性设置需先创建BS对象

        public DrawPanel() {//构造函数
                lineStroke = new BasicStroke(2.0f);
        }

        @Override//方法重写
        protected void paintComponent(Graphics g) {//使用Graphics2D类画图形 通过对pC方法重写 强制把对象g转换成Graphics2D
                draw((Graphics2D) g);
        }

        /*** 绘制
           @param g2d
         */
        private void draw(Graphics2D g2d) {

            Line2D.Double line2D = null;
            Arrow.Attributes arrowAttributes = null;

            for ( int x = 100; x <= 700; x += 120 )

                for ( int y = 100; y <= 700; y += 120 )

                    g2d.drawRect( x, y, 120, 120 );

            // 绘制线的“方向1”箭头
            line2D = new Line2D.Double(220, 220, 340, 340);//起点坐标，终点坐标//西北
            arrowAttributes = new Arrow.Attributes();
            arrowAttributes.angle = 45;
            arrowAttributes.height = 45;
            drawLineArrowDirection1(g2d, arrowAttributes, line2D);

            // 绘制线的“方向2”箭头
            line2D = new Line2D.Double(160, 100, 160, 220);//北
            arrowAttributes = new Arrow.Attributes();
            arrowAttributes.angle = 45;
            arrowAttributes.height = 45;
            drawLineArrowDirection1(g2d, arrowAttributes, line2D);

            line2D = new Line2D.Double(160, 340, 160, 220);//南
            arrowAttributes = new Arrow.Attributes();
            arrowAttributes.angle = 45;
            arrowAttributes.height = 45;
            drawLineArrowDirection1(g2d, arrowAttributes, line2D);

            line2D = new Line2D.Double(220, 160, 340, 160);//西
            arrowAttributes = new Arrow.Attributes();
            arrowAttributes.angle = 45;
            arrowAttributes.height = 45;
            drawLineArrowDirection1(g2d, arrowAttributes, line2D);

            line2D = new Line2D.Double(460, 160, 340, 160);//东
            arrowAttributes = new Arrow.Attributes();
            arrowAttributes.angle = 45;
            arrowAttributes.height = 45;
            drawLineArrowDirection1(g2d, arrowAttributes, line2D);

            line2D = new Line2D.Double(460, 220, 340, 340);//东北
            arrowAttributes = new Arrow.Attributes();
            arrowAttributes.angle = 45;
            arrowAttributes.height = 45;
            drawLineArrowDirection1(g2d, arrowAttributes, line2D);

            line2D = new Line2D.Double(220, 460, 340, 340);//西南
            arrowAttributes = new Arrow.Attributes();
            arrowAttributes.angle = 45;
            arrowAttributes.height = 45;
            drawLineArrowDirection1(g2d, arrowAttributes, line2D);

            line2D = new Line2D.Double(460, 460, 340, 340);//东南
            arrowAttributes = new Arrow.Attributes();
            arrowAttributes.angle = 45;
            arrowAttributes.height = 45;
            drawLineArrowDirection1(g2d, arrowAttributes, line2D);

            // 绘制线的“双向”箭头
            /*line2D = new Line2D.Double(500, 200, 260, 200);
            arrowAttributes = new Arrow.Attributes();
            arrowAttributes.angle = 30;
            arrowAttributes.height = 40;
            drawLineArrowDirectionAll(g2d, arrowAttributes, line2D);*/
        }


        /**绘制线的“方向1”箭头
             * @param g2d
             * @param arrowAttributes 箭头属性
             * @param line2D          线
         */
        private void drawLineArrowDirection1(Graphics2D g2d, Arrow.Attributes arrowAttributes, Line2D.Double line2D) {
            drawLine(g2d, line2D);
            drawArrow(g2d, arrowAttributes, line2D.getP1(), line2D.getP2());
        }

        /**
             * 绘制线的“方向2”箭头
             *
             * @param g2d
             * @param arrowAttributes 箭头属性
             * @param line2D          线
         */
        private void drawLineArrowDirection2(Graphics2D g2d, Arrow.Attributes arrowAttributes, Line2D.Double line2D) {
            drawLine(g2d, line2D);
            drawArrow(g2d, arrowAttributes, line2D.getP2(), line2D.getP1());
        }

        /*** 绘制线
         * @param g2d
         * @param line2D
         */
        private void drawLine(Graphics2D g2d, Line2D.Double line2D) {
            g2d.setColor(Color.BLACK);
            g2d.setStroke(lineStroke);//通过setStroke()方法设置属性
            g2d.draw(line2D);
        }

        /*** 绘制箭头
         * @param g2d
         * @param arrowAttributes 箭头属性
         * @param point1          线的第一个点
         * @param point2          线的第二个点
         */
        private void drawArrow(Graphics2D g2d, Arrow.Attributes arrowAttributes, Point2D point1, Point2D point2) {
            // 获取Arrow实例
            Arrow arrow = getArrow(arrowAttributes, point1, point2);

            // 构建GeneralPath
            GeneralPath arrow2D = new GeneralPath();
            arrow2D.moveTo(arrow.point1.x, arrow.point1.y);
            arrow2D.lineTo(arrow.point2.x, arrow.point2.y);
            arrow2D.lineTo(arrow.point3.x, arrow.point3.y);
            arrow2D.closePath();

            // 绘制
            g2d.setColor(arrow.attributes.color);
            g2d.fill(arrow2D);
        }

        /*** 获取箭头实体类
             * @param arrowAttributes 箭头属性
             * @param point1          线的第一个点
             * @param point2          线的第二个点
             * @return
         */
        private Arrow getArrow(Arrow.Attributes arrowAttributes, Point2D point1, Point2D point2) {
            Arrow arrow = new Arrow(arrowAttributes);

            // 计算斜边
            double hypotenuse = arrow.attributes.height / Math.cos(Math.toRadians(arrow.attributes.angle / 2));

            // 计算当前线所在的象限
            int quadrant = -1;
            if (point1.getX() > point2.getX() && point1.getY() < point2.getY()) {
                    quadrant = 1;
            } else if (point1.getX() < point2.getX() && point1.getY() < point2.getY()) {
                    quadrant = 2;
            } else if (point1.getX() < point2.getX() && point1.getY() > point2.getY()) {
                    quadrant = 3;
            } else if (point1.getX() > point2.getX() && point1.getY() > point2.getY()) {
                    quadrant = 4;
            }

            // 计算线的夹角
            double linAngle = getLineAngle(point1.getX(), point1.getY(), point2.getX(), point2.getY());
            if (Double.isNaN(linAngle)) {
                    // 线与x轴垂直
                if (point1.getX() == point2.getX()) {
                    if (point1.getY() < point2.getY()) {
                        linAngle = 90;
                    } else {
                        linAngle = 270;
                    }
                        quadrant = 2;
                    }
            }
                // 线与y轴垂直
            else if (linAngle == 0) {
                if (point1.getY() == point2.getY()) {
                    if (point1.getX() < point2.getX()) {
                        linAngle = 0;
                    } else {
                        linAngle = 180;
                    }
                        quadrant = 2;
                    }
            }

                // 上侧一半箭头
            double xAngle = linAngle - arrow.attributes.angle / 2; // 与x轴夹角
            double py0 = hypotenuse * Math.sin(Math.toRadians(xAngle)); // 计算y方向增量
            double px0 = hypotenuse * Math.cos(Math.toRadians(xAngle)); // 计算x方向增量

                // 下侧一半箭头
            double yAngle = 90 - linAngle - arrow.attributes.angle / 2; // 与y轴夹角
            double px1 = hypotenuse * Math.sin(Math.toRadians(yAngle));
            double py1 = hypotenuse * Math.cos(Math.toRadians(yAngle));

                // 第一象限
            if (quadrant == 1) {
                px0 = -px0;
                px1 = -px1;

            } else if (quadrant == 2) {
                    // do nothing
            } else if (quadrant == 3) {
                    py0 = -py0;
                    py1 = -py1;

            } else if (quadrant == 4) {
                py0 = -py0;
                px0 = -px0;

                px1 = -px1;
                py1 = -py1;
            }

                // build
            arrow.point1 = new Point2D.Double();
            arrow.point1.x = point1.getX();
            arrow.point1.y = point1.getY();

            arrow.point2 = new Point2D.Double();
            arrow.point2.x = point1.getX() + px0;
            arrow.point2.y = point1.getY() + py0;

            arrow.point3 = new Point2D.Double();
            arrow.point3.x = point1.getX() + px1;
            arrow.point3.y = point1.getY() + py1;

            return arrow;
        }

        /*** 获取线与X轴的夹角
             * @param x1
             * @param y1
             * @param x2
             * @param y2
             * @return
         */
        protected double getLineAngle(double x1, double y1, double x2, double y2) {
            double k1 = (y2 - y1) / (x2 - x1);
            double k2 = 0;
            return Math.abs(Math.toDegrees(Math.atan((k2 - k1) / (1 + k1 * k2))));
            }
    }


    /*** 箭头实体类
     */
    public static class Arrow {
        Attributes attributes;
        Point2D.Double point1;
        Point2D.Double point2;
        Point2D.Double point3;

        public Arrow(Attributes attributes) {
            this.attributes = attributes;
        }

        /*** 箭头属性
         */
        public static class Attributes {
            double height; // 箭头的高度
            double angle; // 箭头角度
            Color color; // 箭头颜色

            public Attributes() {
                this.height = 60;
                this.angle = 30;
                this.color = Color.BLACK;
                }
            }
        }


        public static void main(String[] args) {
            JFrame frame = new JFrame();
            frame.setTitle("流向图");
            Dimension dimension = new Dimension(1000, 1000);
            frame.setSize(dimension);
            frame.setLocationRelativeTo(null);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setContentPane(new DrawPanel());
            frame.setVisible(true);
        }


}
