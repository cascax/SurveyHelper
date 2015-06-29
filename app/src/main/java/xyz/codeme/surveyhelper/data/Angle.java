package xyz.codeme.surveyhelper.data;


public class Angle {
    private int du;
    private int fen;
    private int miao;
    private double realDu;

    public Angle(int du, int fen, int miao) {
        this.du = du;
        this.fen = fen;
        this.miao = miao;
        setRealDu(du, fen, miao);
    }

    public Angle(String du, String fen, String miao) {
        this.du = du==null||du.length()<1 ? 0 : Integer.parseInt(du);
        this.fen = fen==null||fen.length()<1 ? 0 : Integer.parseInt(fen);
        this.miao = miao==null||miao.length()<1 ? 0 : Integer.parseInt(miao);
        setRealDu(this.du, this.fen, this.miao);
    }

    public Angle(double realDu) {
        this.realDu = realDu;
        setDuFenMiao(realDu);
    }

    /**
     * 求两个角度差的绝对值
     * @param  a 另一个角
     * @return   差的绝对值
     */
    public Angle different(Angle a) {
        return new Angle(Math.abs(realDu - a.getRealDu()));
    }

    /**
     * 加上一个角 (改变自身)
     * @param a 另一个角
     */
    public Angle add(Angle a) {
        this.du += a.getDu();
        this.fen += a.getFen();
        this.miao += a.getMiao();
        setRealDu(this.du, this.fen, this.miao);
        return this;
    }

    /**
     * 除一个常数 (改变自身)
     * @param a 另一个角
     */
    public Angle divide(double a) {
        this.realDu /= a;
        setDuFenMiao(this.realDu);
        return this;
    }

    @Override
    public String toString() {
        return du + "°" + fen + "′" + miao + "″";
    }

    public void setDuFenMiao(double realDu) {
        this.du = (int) realDu;
        realDu = (Math.abs(realDu) - Math.abs(du)) * 60;
        this.fen = (int) realDu;
        realDu = (realDu - fen) * 60;
        this.miao = (int) Math.round(realDu);
    }

    public void setRealDu(int du, int fen, int miao) {
        this.realDu = (Math.abs(du) + (fen + miao / 60.0) / 60);
        if(du < 0)
            this.realDu *= -1;
    }

    public double getRealDu() {
        return realDu;
    }

    public int getDu() {
        return du;
    }

    public int getFen() {
        return fen;
    }

    public int getMiao() {
        return miao;
    }
}
