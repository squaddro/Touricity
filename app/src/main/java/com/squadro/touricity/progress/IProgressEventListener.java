package com.squadro.touricity.progress;

public interface IProgressEventListener {
	void ProgressUpdated(Progress progress);
	void ProgressFinished();
}
