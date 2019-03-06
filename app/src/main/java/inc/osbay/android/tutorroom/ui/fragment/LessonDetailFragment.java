package inc.osbay.android.tutorroom.ui.fragment;

import android.app.Fragment;
import android.app.FragmentManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import inc.osbay.android.tutorroom.R;
import inc.osbay.android.tutorroom.sdk.model.Lesson;

public class LessonDetailFragment extends BackHandledFragment {

    @BindView(R.id.tool_bar)
    Toolbar toolBar;
    @BindView(R.id.lesson_cover)
    SimpleDraweeView lessonCover;
    @BindView(R.id.lesson_title)
    TextView lessonTitle;
    @BindView(R.id.lesson_desc)
    TextView lessonDesc;
    @BindView(R.id.class_min)
    TextView classMin;
    @BindView(R.id.price_tv)
    TextView price;
    private Lesson lesson;
    private String tagID;
    private String packageID;
    private String lessonType;

    @Override
    public boolean onBackPressed() {
        getFragmentManager().popBackStack();
        return false;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        lesson = (Lesson) getArguments().getSerializable("lesson");
        tagID = getArguments().getString("tag_id");
        lessonType = getArguments().getString("lesson_type");
        if (tagID.equalsIgnoreCase("0")) packageID = getArguments().getString("package_id");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_lesson_detail, container, false);
        ButterKnife.bind(this, view);

        toolBar.setBackgroundColor(getActivity().getResources().getColor(R.color.colorPrimary));
        setSupportActionBar(toolBar);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        lessonCover.setImageURI(Uri.parse(lesson.getLessonCover()));
        lessonTitle.setText(lesson.getLessonName());
        lessonDesc.setText(lesson.getLessonDescription());
        classMin.setText(getString(R.string.minute, String.valueOf(lesson.getClassMin())));
        price.setText(String.valueOf(lesson.getLessonPrice()));
    }

    @Override
    public void onStart() {
        super.onStart();
        setHasOptionsMenu(true);
        setTitle(getString(R.string.lesson_detail));
        setDisplayHomeAsUpEnable(true);
    }

    @OnClick(R.id.book_btn_tv)
    void bookLesson() {
        FragmentManager fm = getFragmentManager();
        Fragment frg = fm.findFragmentById(R.id.framelayout);
        Fragment fragment = new SingleBookingChooseLessonFragment();

        Bundle bundle = new Bundle();
        bundle.putString(SingleBookingChooseLessonFragment.Booking_EXTRA, LessonDetailFragment.class.getSimpleName());
        bundle.putString("lesson_id", lesson.getLessonId());
        bundle.putString("lesson_type", lessonType);
        bundle.putString("tag_id", tagID);
        if (tagID.equalsIgnoreCase("0")) bundle.putString("package_id", packageID);
        fragment.setArguments(bundle);
        if (frg == null) {
            fm.beginTransaction()
                    .add(R.id.framelayout, fragment)
                    .commit();
        } else {
            fm.beginTransaction()
                    .addToBackStack(null)
                    .replace(R.id.framelayout, fragment)
                    .commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return true;
    }
}
