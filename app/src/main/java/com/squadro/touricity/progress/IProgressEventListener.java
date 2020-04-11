package com.squadro.touricity.progress;

public interface IProgressEventListener {
	void progressUpdated(Progress progress);
	void progressFinished();
}
