///Author: Danyang Wang
//Class: ICS4U
//Date: Jan 2nd, 2020
//Instructor: Mr Radulovic
//Assignment name: ICS4U Culminating
/*Description: This program implements a 4x4 matrix object which uses a 2d array 
 * to store the elements. Additionally, it contains the operations and transformations 
 * of matrix. (Not used because we decided to use legacyGL)
*/
public class Matrix4f implements Matrix4f_Interface {

	private float[][] data;

	//Constructors
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

	public float[][] getData() {
		return data;
	}

	// perform matrix addition 
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
	// perform matrix subtraction 
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
	// perform matrix multiplication
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
	// creates an identity matrix
	public Matrix4f identity() {
		float[][] d = new float[4][4];
		for (int i = 0; i < 4; i++) {
			d[i][i] = 1;
		}

		Matrix4f m = new Matrix4f(d);
		return m;
	}

	@Override
	// represent translation using matrix
	public Matrix4f translate(Point3f p) {
		float[][] translation_matrix = new float[4][4];
		translation_matrix[0][0] = 1;
		translation_matrix[0][3] = p.getX();
		translation_matrix[1][1] = 1;
		translation_matrix[1][3] = p.getY();
		translation_matrix[2][2] = 1;
		translation_matrix[2][3] = p.getZ();
		translation_matrix[3][3] = 1;

		Matrix4f m = new Matrix4f(translation_matrix);
		return m;
	}

	// represent rotation using matrix
	// using euler's angles
	@Override
	public Matrix4f rotate(Point3f p) {
		float x = (float) Math.toRadians(p.getX());
		float y = (float) Math.toRadians(p.getY());
		float z = (float) Math.toRadians(p.getZ());

		float[][] rotZ = new float[4][4];
		rotZ[0][0] = (float) Math.cos(z);
		rotZ[0][1] = -(float) Math.sin(z);
		rotZ[1][0] = (float) Math.sin(z);
		rotZ[1][1] = (float) Math.cos(z);
		rotZ[2][2] = 1;
		rotZ[3][3] = 1;
		Matrix4f mz = new Matrix4f(rotZ);

		float[][] rotY = new float[4][4];
		rotY[0][0] = (float) Math.cos(y);
		rotY[0][2] = -(float) Math.sin(y);
		rotY[1][1] = 1;
		rotY[2][0] = (float) Math.sin(y);
		rotY[2][2] = (float) Math.cos(y);
		rotY[3][3] = 1;
		Matrix4f my = new Matrix4f(rotY);

		float[][] rotX = new float[4][4];
		rotX[0][0] = 1;
		rotX[1][1] = (float) Math.cos(x);
		rotX[1][2] = -(float) Math.sin(x);
		rotX[2][1] = (float) Math.sin(x);
		rotX[2][2] = (float) Math.cos(x);
		rotX[3][3] = 1;
		Matrix4f mx = new Matrix4f(rotX);

		Matrix4f m = mz.multiply(my).multiply(mx);
		return m;
	}

	@Override
	// represent scaling using matrix
	public Matrix4f scale(Point3f p) {
		float[][] scale_matrix = new float[4][4];
		scale_matrix[0][0] = p.getX();
		scale_matrix[1][1] = p.getY();
		scale_matrix[2][2] = p.getZ();
		scale_matrix[3][3] = 1;

		Matrix4f m = new Matrix4f(scale_matrix);
		return m;
	}

	@Override
	// finds the inverse of the current matrix
	// visited geeks for geeks
	public Matrix4f findInverse() {
		float[][] ans = new float[4][4];
		float[][] a = data;
		float[][] b = identity().getData();
		float[] c = new float[4];
		int[] index = new int[4];
		for (int i = 0; i < 4; i++) {
			index[i] = i;
		}
		// Transform matrix into an upper triangle

		// Method to carry out the partial-pivoting Gaussian
		// elimination. Here index[] stores pivoting order.

		// Find the rescaling factors, one from each row
		for (int i = 0; i < 4; i++) {
			float c1 = 0;
			for (int j = 0; j < 4; j++) {
				float c0 = Math.abs(a[i][j]);
				if (c0 > c1)
					c1 = c0;
			}
			c[i] = c1;
		}

		// Search the pivoting element from each column
		int k = 0;
		for (int j = 0; j < 3; j++) {
			double pi1 = 0;
			for (int i = j; i < 4; i++) {
				double pi0 = Math.abs(a[index[i]][j]);
				pi0 /= c[index[i]];
				if (pi0 > pi1) {
					pi1 = pi0;
					k = i;
				}
			}

			// Interchange rows according to the pivoting order
			int itmp = index[j];
			index[j] = index[k];
			index[k] = itmp;
			for (int i = j + 1; i < 4; i++) {
				float pj = a[index[i]][j] / a[index[j]][j];

				// Record pivoting ratios below the diagonal
				a[index[i]][j] = pj;

				// Modify other elements accordingly
				for (int l = j + 1; l < 4; l++)
					a[index[i]][l] -= pj * a[index[j]][l];
			}
		}

		// Update the matrix b[i][j] with the ratios stored
		for (int i = 0; i < 3; i++)
			for (int j = i + 1; j < 4; j++)
				for (int p = 0; p < 4; ++p)
					b[index[j]][p] -= a[index[j]][i] * b[index[i]][p];

		// Perform backward substitutions
		for (int i = 0; i < 4; i++) {
			ans[3][i] = b[index[3]][i] / a[index[3]][3];
			for (int j = 2; j >= 0; --j) {
				ans[j][i] = b[index[j]][i];
				for (int p = j + 1; p < 4; ++p) {
					ans[j][i] -= a[index[j]][p] * ans[p][i];
				}
				ans[j][i] /= a[index[j]][j];
			}
		}
		Matrix4f m = new Matrix4f(ans);
		return m;
	}

}
