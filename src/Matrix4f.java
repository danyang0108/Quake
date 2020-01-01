
public class Matrix4f implements Matrix4f_Interface{

	private float[][] data;

	public Matrix4f() {
		data = new float[4][4];
	}

	public Matrix4f(float[][] m) {
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				data[i][j] = m[i][j];
			}
		}
	}
	
	public float[][] getData(){
		return data;
	}
	@Override
	public Matrix4f add(Matrix4f m) {
		float[][] mData = m.getData();
		float[][] newData = new float[4][4];
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				newData[i][j] = mData[i][j] + data[i][j];
			}
		}
		
		Matrix4f result = new Matrix4f(newData);
		return result;
	}

	@Override
	public Matrix4f subtract(Matrix4f m) {
		float[][] mData = m.getData();
		float[][] newData = new float[4][4];
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				newData[i][j] = data[i][j] - mData[i][j];
			}
		}
		
		Matrix4f result = new Matrix4f(newData);
		return result;
	}

	@Override
	public Matrix4f multiply(Matrix4f m) {
		float[][] mData = m.getData();
		float[][] newData = new float[4][4];
		for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                for (int k = 0; k < 4; k++) {
                    newData[i][j] += (data[i][k] * mData[k][j]);
                }
            }
		}
		
		Matrix4f result = new Matrix4f(newData);
		return result;
	}

	@Override
	public Matrix4f identity() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Matrix4f translate(Point3f p) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Matrix4f rotate(Point3f p) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Matrix4f scale(Point3f p) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Matrix4f findInverse() {
		// TODO Auto-generated method stub
		return null;
	}
	

}
