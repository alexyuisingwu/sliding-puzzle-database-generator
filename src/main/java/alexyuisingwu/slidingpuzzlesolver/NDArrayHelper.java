package alexyuisingwu.slidingpuzzlesolver;

// NOTE: not using nd4j because:
// ndfj doesn't support byte and boolean arrays or bitset
// using NDArray would require boxing primitives to use generics (more memory/time use)
public class NDArrayHelper {

    private final int size;
    private final int[] shape;

    private final int[] strides;

    public NDArrayHelper(int size, int[] shape) {

        this.size = size;
        this.shape = shape;

        this.strides = new int[shape.length];

        int stride = 1;
        for (int i = strides.length - 1; i >= 0; i--) {
            this.strides[i] = stride;
            stride *= shape[i];
        }
    }

    public int getIndex(byte[] indices) {
        int index = 0;
        for (int i = 0; i < indices.length; i++) {
            index += this.strides[i] * indices[i];
        }
        return index;
    }

    public int getIndex(int[] indices) {
        int index = 0;
        for (int i = 0; i < indices.length; i++) {
            index += this.strides[i] * indices[i];
        }
        return index;
    }

}
