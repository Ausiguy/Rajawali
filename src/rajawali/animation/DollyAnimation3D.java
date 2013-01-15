package rajawali.animation;

import android.util.FloatMath;
import rajawali.BaseObject3D;
import rajawali.animation.ISpline;
import rajawali.animation.TranslateAnimation3D;
import rajawali.math.Number3D;

public class DollyAnimation3D extends TranslateAnimation3D {

	private BaseObject3D objectToFollow;
	private float followObjectUpAboveHeight;
	private float followObjectDownBelowHeight;
	private Number3D prevPoint;
	private Number3D lastPoint;

	
	public DollyAnimation3D(Number3D fromPosition, Number3D toPosition, BaseObject3D objectToFollow) {
		this(fromPosition, toPosition, objectToFollow, 0, 0);
	}

	public DollyAnimation3D(ISpline splinePath, BaseObject3D objectToFollow) {
		this(splinePath, objectToFollow, 0, 0);
	}

	public DollyAnimation3D(Number3D fromPosition, Number3D toPosition, BaseObject3D objectToFollow, float followObjectUpAboveHeight, float followObjectDownBelowHeight) {
		super(fromPosition, toPosition);
		this.objectToFollow = objectToFollow;
		this.followObjectUpAboveHeight = followObjectUpAboveHeight;
		this.followObjectDownBelowHeight = followObjectDownBelowHeight;
	}

	public DollyAnimation3D(ISpline splinePath, BaseObject3D objectToFollow, float followObjectUpAboveHeight, float followObjectDownBelowHeight) {
		super(splinePath);
		this.objectToFollow = objectToFollow;
		this.followObjectUpAboveHeight = followObjectUpAboveHeight;
		this.followObjectDownBelowHeight = followObjectDownBelowHeight;
	}

	@Override
	protected void applyTransformation(float interpolatedTime) {

		if (interpolatedTime >= 0 && interpolatedTime <= 1) {
			if (mSplinePath == null) {
				if (mDiffPosition == null)
					mDiffPosition = Number3D.subtract(mToPosition, mFromPosition);
				mMultipliedPosition.setAllFrom(mDiffPosition);
				mMultipliedPosition.multiply(interpolatedTime);
				mAddedPosition.setAllFrom(mFromPosition);
				mAddedPosition.add(mMultipliedPosition);
				Number3D dollyPosition = new Number3D(objectToFollow.getPosition());
				float height = dollyPosition.y;
				if (followObjectUpAboveHeight > 0 && height > followObjectUpAboveHeight) {
					dollyPosition.y = followObjectUpAboveHeight + FloatMath.sqrt(height - followObjectUpAboveHeight);
				}
				if (followObjectDownBelowHeight < 0 && height < followObjectDownBelowHeight) {
					dollyPosition.y = followObjectDownBelowHeight - FloatMath.sqrt(height - followObjectDownBelowHeight);
				}
				mAddedPosition.add(dollyPosition);
				mTransformable3D.getPosition().setAllFrom(mAddedPosition);
			} else {
				Number3D pathPoint = mSplinePath.calculatePoint(interpolatedTime);
				Number3D dollyPosition = new Number3D(objectToFollow.getPosition());
				float height = dollyPosition.y;
				if (followObjectUpAboveHeight > 0 && height > followObjectUpAboveHeight) {
					dollyPosition.y = followObjectUpAboveHeight + FloatMath.sqrt(height - followObjectUpAboveHeight);
				}
				if (followObjectDownBelowHeight < 0 && height < followObjectDownBelowHeight) {
					dollyPosition.y = followObjectDownBelowHeight - FloatMath.sqrt(height - followObjectDownBelowHeight);
				}
				pathPoint.add(dollyPosition);
				mTransformable3D.getPosition().setAllFrom(pathPoint);
	
				if (mOrientToPath) {
					mTransformable3D.setLookAt(mSplinePath.getCurrentTangent());
				}
			}
		} else {
			if (prevPoint != null) {
				// extrapolate from the last two points if we have gone over
				Number3D diff = Number3D.subtract(lastPoint, prevPoint);
				mTransformable3D.getPosition().add(diff);
			}
		}
		prevPoint = lastPoint;
		lastPoint = new Number3D(mTransformable3D.getPosition());
		
	}
	
	public void setObjectToFollow(BaseObject3D objectToFollow) {
		
		this.objectToFollow = objectToFollow;
	}
	
}
