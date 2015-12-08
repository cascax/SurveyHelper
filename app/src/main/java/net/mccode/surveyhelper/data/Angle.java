package net.mccode.surveyhelper.data;

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

    public Angle(Angle a) {
        this.du = a.getDu();
        this.fen = a.getFen();
        this.miao = a.getMiao();
        this.realDu = a.getRealDu();
    }

    /**
     * 加一个角度 (改变自身)
     * @param a 另一个角
     */
    public Angle add(Angle a) {
        this.realDu += a.getRealDu();
        setDuFenMiao(this.realDu);
        return this;
    }

    /**
     * 减一个角度 (改变自身)
     * @param a 另一个角
     */
    public Angle subtract(Angle a) {
        this.realDu -= a.getRealDu();
        setDuFenMiao(this.realDu);
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

    public Angle copy() {
        return new Angle(this);
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
