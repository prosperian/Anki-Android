
package com.ichi2.anki.dialogs;

import android.content.res.Resources;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.afollestad.materialdialogs.MaterialDialog;
import com.ichi2.anki.R;
import com.ichi2.anki.analytics.AnalyticsDialogFragment;
import com.ichi2.utils.BundleUtils;

public class ExportDialog extends AnalyticsDialogFragment {

    public interface ExportDialogListener {

        void exportApkg(String path, Long did, boolean includeSched, boolean includeMedia);
        void dismissAllDialogFragments();
    }

    private final int INCLUDE_SCHED = 0;
    private final int INCLUDE_MEDIA = 1;
    private boolean mIncludeSched = false;
    private boolean mIncludeMedia = false;


    /**
     * Creates a new instance of ExportDialog to export a deck of cards
     *
     * @param did A long which specifies the deck to be exported,
     *            if did is null then the whole collection of decks will be exported
     * @param dialogMessage A string which can be used to show a custom message or specify import path
     */
    public static ExportDialog newInstance(@NonNull String dialogMessage, @Nullable Long did) {
        ExportDialog f = new ExportDialog();
        Bundle args = new Bundle();
        if (did != null) {
            args.putLong("did", did);
        }
        args.putString("dialogMessage", dialogMessage);
        f.setArguments(args);
        return f;
    }

    /**
     * Creates a new instance of ExportDialog to export the user collection of decks
     *
     * @param dialogMessage A string which can be used to show a custom message or specify import path
     */
    public static ExportDialog newInstance(@NonNull String dialogMessage) {
        return newInstance(dialogMessage, null);
    }


    @NonNull
    @Override
    public MaterialDialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Resources res = getResources();
        final Long did = BundleUtils.getNullableLong(getArguments(), "did");
        Integer[] checked;
        if (did != null) {
            mIncludeSched = false;
            checked = new Integer[]{};
        } else {
            mIncludeSched = true;
            checked = new Integer[]{ INCLUDE_SCHED };
        }
        final String[] items = { res.getString(R.string.export_include_schedule),
                res.getString(R.string.export_include_media) };

        MaterialDialog.Builder builder = new MaterialDialog.Builder(getActivity())
                .title(R.string.export)
                .content(getArguments().getString("dialogMessage"))
                .positiveText(android.R.string.ok)
                .negativeText(android.R.string.cancel)
                .cancelable(true)
                .items(items)
                .alwaysCallMultiChoiceCallback()
                .itemsCallbackMultiChoice(checked,
                        (materialDialog, integers, charSequences) -> {
                            mIncludeMedia = false;
                            mIncludeSched = false;
                            for (Integer integer : integers) {
                                switch (integer) {
                                    case INCLUDE_SCHED:
                                        mIncludeSched = true;
                                        break;
                                    case INCLUDE_MEDIA:
                                        mIncludeMedia = true;
                                        break;
                                }
                            }
                            return true;
                        })
                .onPositive((dialog, which) -> {
                    ((ExportDialogListener) getActivity()).exportApkg(null, did, mIncludeSched, mIncludeMedia);
                    dismissAllDialogFragments();
                })
                .onNegative((dialog, which) -> dismissAllDialogFragments());
        return builder.show();
    }


    public void dismissAllDialogFragments() {
        ((ExportDialogListener) getActivity()).dismissAllDialogFragments();
    }

}
