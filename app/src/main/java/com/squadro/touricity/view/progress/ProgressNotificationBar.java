package com.squadro.touricity.view.progress;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;

import com.squadro.touricity.R;
import com.squadro.touricity.progress.IProgressEventListener;
import com.squadro.touricity.progress.Progress;

public class ProgressNotificationBar implements IProgressEventListener {

	// This is the Notification Channel ID. More about this in the next section
	public static final String NOTIFICATION_CHANNEL_ID = "channel_id";
	//User visible Channel Name
	public static final String CHANNEL_NAME = "Notification Channel";
	// Importance applicable to all the notifications in this Channel
	int importance = NotificationManager.IMPORTANCE_DEFAULT;
	// Unique identifier for notification
	public static final int NOTIFICATION_ID = 101;

	Context context;
	NotificationManager notificationManager;

	public ProgressNotificationBar(Context context) {
		this.context = context;
	}

	@RequiresApi(api = Build.VERSION_CODES.O)
	@Override
	public void progressUpdated(Progress progress) {

		NotificationCompat.Builder builder = new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID);

		String timePercentageText = "Progress: %" + String.format("%.1f", progress.getProgressTimePercentage() * 100);
		String distancePercentageText = "Distance: %" + String.format("%.1f", progress.getProgressDistancePercentage() * 100);
		String titleText = (progress.isOnPath() ? "Next Place: " : "On Place: ") + progress.getNextPlaceTitle();
		double nextPlaceTime = progress.getTimeToNextPlace();
		int nextPlaceMinutes = (int) nextPlaceTime;
		int nextPlaceSeconds = (int)((nextPlaceTime - (double) nextPlaceMinutes) * 60);
		String nextPlaceTimeText = "Time to next place: " + String.format("%02d", nextPlaceMinutes) + ":" + String.format("%02d", nextPlaceSeconds);
		if(!progress.isOnPath()) {
			nextPlaceTimeText = "You can spend " + progress.getCurrentEntry().getDuration() + " minutes";
		}
		String startTimeText = "Started: " + progress.getStartTime().toString();
		String actualEndTimeText = "actual: " + progress.getActualEndTime().toString();
		String expectedEndTimeText = "Expected Finish: " + progress.getExpectedFinishTime().toString();

		builder.setContentTitle(titleText);
		builder.setContentText(nextPlaceTimeText);

		builder.setStyle(new NotificationCompat.InboxStyle()
				.setSummaryText(timePercentageText)
				.setBigContentTitle(titleText)
				.addLine(nextPlaceTimeText)
				.addLine(distancePercentageText)
				.addLine(startTimeText)
				.addLine(expectedEndTimeText));

		builder.setSmallIcon(R.drawable.position_marker);
		builder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.position_marker));
		builder.setPriority(NotificationCompat.PRIORITY_HIGH);
		builder.setVisibility(NotificationCompat.VISIBILITY_PUBLIC);
		builder.setOnlyAlertOnce(true);
		builder.setOngoing(true);

		Notification notification = builder.build();

		notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

		NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "Channel human readable title", NotificationManager.IMPORTANCE_DEFAULT);
		notificationManager.createNotificationChannel(channel);
		notificationManager.notify(0, notification);
	}

	@Override
	public void progressFinished() {
		if(notificationManager != null)
			notificationManager.cancel(0);
	}
}
