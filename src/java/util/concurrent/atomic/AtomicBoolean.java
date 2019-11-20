/*
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 */

/*
 *
 *
 *
 *
 * 在f JCP JSR-166专家组的帮助下，由Doug Lea编写并发布到开放域
 * Written by Doug Lea with assistance from members of JCP JSR-166
 * Expert Group and released to the public domain, as explained at
 * http://creativecommons.org/publicdomain/zero/1.0/
 */

package java.util.concurrent.atomic;
import sun.misc.Unsafe;

/**
 * A {@code boolean} value that may be updated atomically. See the
 * {@link java.util.concurrent.atomic} package specification for
 * description of the properties of atomic variables. An
 * {@code AtomicBoolean} is used in applications such as atomically
 * updated flags, and cannot be used as a replacement for a
 * {@link java.lang.Boolean}.
 *
 * @since 1.5
 * @author Doug Lea
 */
public class AtomicBoolean implements java.io.Serializable {

    private static final long serialVersionUID = 4654671469794556979L;

    /***
     * 定义一个内存操作对象。
     */
    private static final Unsafe unsafe = Unsafe.getUnsafe();

    /***
     * 存放的位移偏移量
     */
    private static final long valueOffset;

    /**
     * 获取当前值所在内存中的位移偏移量
     */
    static {
        try {
            valueOffset = unsafe.objectFieldOffset
                (AtomicBoolean.class.getDeclaredField("value"));
        } catch (Exception ex) { throw new Error(ex); }
    }

    /***
     * 定义一个多线程，有序的，可见的值
     * 注意volatile主要是乐观锁的实现方式
     */
    private volatile int value;

    /***
     * 赋值bool类型。true为1，false 为0
     * @param initialValue
     */
    public AtomicBoolean(boolean initialValue) {
        value = initialValue ? 1 : 0;
    }

    /**
     * 新建一个bool类型，不进行初始化value值
     */
    public AtomicBoolean() {
    }

    /**
     * 获取传入的值
     */
    public final boolean get() {
        return value != 0;
    }

    /**
     * 比较并且交换该值，乐观锁实现
     * 如果当前线程获取的值是跟{expect}相同，尼玛就更换为update的值
     * 同时返回该对象更新前的值。
     * eg: 更新前 {oldValue = false}, {expect=true},{update=false}
     * 执行由于 expect！=oldValue，故不执行，直接返回{oldValue=false},
     *  {value=OldValue}
     * eg： 更新前 {oldValue = true}, {expect=true},{update=false},
     *  执行由于 expect==oldValue，故执行，返回{oldValue=false}，同时
     *  {value =  update}
     */
    public final boolean compareAndSet(boolean expect, boolean update) {
        int e = expect ? 1 : 0;
        int u = update ? 1 : 0;
        return unsafe.compareAndSwapInt(this, valueOffset, e, u);
    }

    /**
     * 比较并且交换该值，乐观锁实现
     * 如果当前线程获取的值是跟{expect}相同，尼玛就更换为update的值
     * 同时返回该对象更新前的值。
     * eg: 更新前 {oldValue = false}, {expect=true},{update=false}
     * 执行由于 expect！=oldValue，故不执行，直接返回{oldValue=false},
     *  {value=OldValue}
     * eg： 更新前 {oldValue = true}, {expect=true},{update=false},
     *  执行由于 expect==oldValue，故执行，返回{oldValue=false}，同时
     *  {value =  update}
     */
    public boolean weakCompareAndSet(boolean expect, boolean update) {
        int e = expect ? 1 : 0;
        int u = update ? 1 : 0;
        return unsafe.compareAndSwapInt(this, valueOffset, e, u);
    }

    /**
     * 设置新的{value}
     */
    public final void set(boolean newValue) {
        value = newValue ? 1 : 0;
    }

    /***
     * 只需要保证有序条件下更新数据
     * @param newValue
     */
    public final void lazySet(boolean newValue) {
        int v = newValue ? 1 : 0;
        unsafe.putOrderedInt(this, valueOffset, v);
    }

    /***
     *  将值更新为{newValue},如果{value}!={newValue}就替换
     * @param newValue
     * @return
     */
    public final boolean getAndSet(boolean newValue) {
        boolean prev;
        do {
            prev = get();
        } while (!compareAndSet(prev, newValue));
        return prev;
    }

    /**
     * Returns the String representation of the current value.
     * @return the String representation of the current value
     */
    public String toString() {
        return Boolean.toString(get());
    }

}
