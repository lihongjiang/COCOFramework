package com.cocosw.framework.uiquery;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.res.Resources.NotFoundException;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import com.androidquery.AbstractAQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.androidquery.callback.BitmapAjaxCallback;
import com.androidquery.callback.ImageOptions;
import com.androidquery.util.AQUtility;
import com.androidquery.util.Common;
import com.androidquery.util.XmlDom;
import com.cocosw.accessory.utils.ImageUtils;
import com.cocosw.framework.R;
import com.squareup.picasso.Picasso;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;
import org.xml.sax.SAXException;

import java.io.File;
import java.util.WeakHashMap;

public class CocoQuery extends AbstractAQuery<CocoQuery> {

	private OnClickListener dialogClicklistener;
	private Activity act;
	private final WeakHashMap<Integer, CocoTask<?>> taskpool = new WeakHashMap<Integer, CocoTask<?>>();
	private Fragment fragment;

	public CocoQuery(final View view) {
		super(view);
	}

	public CocoQuery(final Activity act) {
		super(act);
		this.act = act;
	}

	public CocoQuery(final Activity act, final View root) {
		super(act, root);
		this.act = act;
	}

	public CocoQuery(final Activity act, final Fragment fragment,
			final View root) {
		super(act, root);
		this.act = act;
		this.fragment = fragment;
	}

	private static final int TOAST_DISPLAY_TIME = 3000;

	public CocoQuery(final Context context) {
		super(context);

	}

	public CocoQuery alert(final int title, final String message) {
		return alert(getContext().getResources().getString(title), message);
	}

	public CocoQuery alert(final String title, final CharSequence message) {
		final AlertDialog.Builder builder = new AlertDialog.Builder(
				getContext());
		builder.setTitle(title);
		builder.setMessage(message);
		builder.setIcon(android.R.drawable.ic_dialog_alert);
		builder.setPositiveButton(R.string.ok,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(final DialogInterface dialog,
							final int which) {
						if (dialogClicklistener != null) {
							dialogClicklistener.onClick(dialog, which);
						}
						final AlertDialog ad = builder.create();
						ad.cancel();
					}
				});
		builder.show();
		return this;
	}

	public CocoQuery alert(final int title, final int message) {
		new AlertDialog.Builder(getContext());
		return alert(getContext().getString(title),
				getContext().getString(message));
	}

	public CocoQuery alert(final Exception e) {
		alert(getContext().getString(R.string.info), e.getMessage());
		return this;
	}

	public CocoQuery confirm(final int title, final int message,
			final OnClickListener onClickListener) {
		final AlertDialog.Builder builder = new AlertDialog.Builder(
				getContext());
		builder.setTitle(title);
		builder.setIcon(android.R.drawable.ic_dialog_info);
		builder.setMessage(message);

		builder.setPositiveButton(R.string.ok,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(final DialogInterface dialog,
							final int which) {
						if (onClickListener != null) {
							onClickListener.onClick(dialog, which);
						}

					}
				});
		builder.setNegativeButton(R.string.cancel,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(final DialogInterface dialog,
							final int which) {
						if (onClickListener != null) {
							onClickListener.onClick(dialog, which);
						}
					}
				});
		builder.show();
		return this;
	}

	public CocoQuery diaclicked(
			final DialogInterface.OnClickListener onClickListener) {
		dialogClicklistener = onClickListener;
		return this;
	}

	public CocoQuery alert(final int title, final int message,
			final DialogInterface.OnClickListener onClickListener) {

		final AlertDialog.Builder builder = new AlertDialog.Builder(
				getContext());
		builder.setTitle(title);
		builder.setMessage(message);
		builder.setIcon(android.R.drawable.ic_dialog_alert);
		builder.setPositiveButton(R.string.ok,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(final DialogInterface dialog,
							final int which) {
						if (onClickListener != null) {
							onClickListener.onClick(dialog, which);
						}
						final AlertDialog ad = builder.create();
						ad.cancel();
					}
				});
		builder.show();
		return this;
	}

	/**
	 * 快速显示一个toast
	 * 
	 * @param message
	 *            消息内容
	 * @param duration
	 *            显示时间
	 * @return
	 */
	public CocoQuery toast(final String message, final int duration) {
		Toast.makeText(getContext(), message, duration).show();
		return this;
	}

	public CocoQuery toast(final int stringId) {
		Toast.makeText(getContext(),
				getContext().getResources().getString(stringId),
				CocoQuery.TOAST_DISPLAY_TIME).show();
		return this;
	}

	public CocoQuery toast(final String message) {
		Toast.makeText(getContext(), message, CocoQuery.TOAST_DISPLAY_TIME)
				.show();
		return this;
	}

	public CocoQuery task(final CocoTask<?> callback) {
		if (progress != null) {
			callback.progress(progress);
		}
		taskpool.put(callback.hashCode(), callback);
		callback.async(act);
		return this;
	}

	public CocoQuery task(final CocoTask<?> task,
			final CocoQueryCallBack callback) {
		if (progress != null) {
			task.progress(progress);
		}
		taskpool.put(callback.hashCode(), task);
		task.setCallback(callback);
		task.async(act);
		return this;
	}

	/**
	 * pager的adapter
	 * 
	 * @param mAdapter
	 * @return
	 */
	public CocoQuery adapter(final PagerAdapter mAdapter) {
		if (view instanceof ViewPager) {
			((ViewPager) view).setAdapter(mAdapter);
		}
		return this;
	}

	public CocoQuery clicked(final CocoTask<?> callback) {

		final Common common = new Common() {
			@Override
			public void onClick(final View v) {
				CocoQuery.this.task(callback);
			}
		};
		return clicked(common);

	}

	public XmlDom xml(final int id) throws NotFoundException, SAXException {
		return new XmlDom(getContext().getResources().openRawResource(id));
	}

	public void toast(final Exception e) {
		toast(e.getMessage());
	}

	public CocoQuery info(final int info) {
		if (act != null) {
			Crouton.makeText(act, info, Style.INFO).show();
		}
		return this;
	}

	public CocoQuery alert(final int info) {
		if (act != null) {
			Crouton.makeText(act, info, Style.ALERT).show();
		}
		return this;
	}

	public CocoQuery crouton(final int info, final Style style) {
		if (act != null) {
			Crouton.makeText(act, info, style).show();
		}
		return this;
	}

	public CocoQuery alert(final CharSequence info) {
		if (act != null & info != null) {
			Crouton.makeText(act, info, Style.ALERT).show();
		}
		return this;
	}

	public CocoQuery confirm(final int info) {
		if (act != null) {
			Crouton.makeText(act, info, Style.CONFIRM).show();
		}
		return this;
	}

	public CocoQuery confirm(final CharSequence info) {
		if (act != null & info != null) {
			Crouton.makeText(act, info, Style.CONFIRM).show();
		}
		return this;
	}

	public CocoQuery post(final Runnable r) {
		AQUtility.post(r);
		return this;
	}


	/**
	 * 终结所有的cocotask
	 * 
	 * @return
	 */
	public CocoQuery CleanAllTask() {
		for (final CocoTask<?> reference : taskpool.values()) {
			if (reference != null) {
				reference.cancle();
			}
		}
		return this;
	}

	public CocoQuery html(final String text) {
		if (TextUtils.isEmpty(text)) {
			return text("");
		}
		if (text.contains("<") && text.contains(">")) {
			getTextView().setMovementMethod(LinkMovementMethod.getInstance());
			return text(Html.fromHtml(text));
		} else {
			return text(text);
		}
	}

	public CocoQuery image(final Bitmap bm, final ImageOptions option) {
		final BitmapAjaxCallback cb = new BitmapAjaxCallback();
		cb.anchor(option.anchor).ratio(option.ratio).round(option.round)
				.bitmap(bm);
		return image(cb);
	}

	/**
	 * 同步下载文件
	 * 
	 * @param url
	 */
	public File downloadFile(final String url, final String folder) {

		final AjaxCallback<File> cb = new AjaxCallback<File>();
		cb.url(url).type(File.class)
				.targetFile(new File(folder + getFilenameFromUrl(url)));
		sync(cb);
		return cb.getResult();
	}

	private String getFilenameFromUrl(final String url) {
		return url.substring(url.lastIndexOf("/"));
	}

	public AbstractAQuery<CocoQuery> image(final String thumbnail,
			final String target, final BitmapAjaxCallback imageCallback) {
		if (TextUtils.isEmpty(thumbnail)) {
			gone();
			return this;
		} else {
			visible();
		}

		// 如果已经下载完成了
		if (getCachedFile(target) != null) {
			image(getCachedFile(target), 0);
			imageCallback.imageView(getImageView());
			imageCallback.callback(target, getCachedImage(target),
					new AjaxStatus(200, null));
		} else {
			image(getCachedImage(thumbnail));
			image(target, true, true, 0, 0, imageCallback);
		}

		return this;
	}


    public CocoQuery image(final String url,final int maxH,final int maxW,final int viewholder) {
        image(url, true, true, 0, viewholder, new BitmapAjaxCallback() {

            @Override
            protected void callback(String url, ImageView iv, Bitmap bm, AjaxStatus status) {
                if (bm != null) {
                    Bitmap result = ImageUtils.scale(bm, AQUtility.dip2pixel(getContext(), maxW), AQUtility.dip2pixel(getContext(), maxH));
//                    if (result != bm) {
//                        bm.recycle();
//                    }
                    iv.setImageBitmap(result);
                }
            }
        });
        return this;
    }

    @Override
    public CocoQuery image(final String url) {
        Picasso.with(getContext()).load(url).into(getImageView());
        return this;
    }

    @Override
    public CocoQuery image(int resid) {
        Picasso.with(getContext()).load(resid).into(getImageView());
        return this;
    }

    @Deprecated
    @Override
    public CocoQuery image(String url, boolean memCache, boolean fileCache) {
        Picasso.with(getContext()).load(url).into(getImageView());
        return this;
    }

    public CocoQuery image(String url, boolean cache) {
        if (cache) {
            image(url);
        } else {
            Picasso.with(getContext()).load(url).skipMemoryCache().into(getImageView());
        }
        return this;
    }

    public CocoQuery image(String url, int holder) {
            Picasso.with(getContext()).load(url).placeholder(holder).into(getImageView());
        return this;
    }

    public CocoQuery image(String url, Drawable holder) {
        Picasso.with(getContext()).load(url).placeholder(holder).into(getImageView());
        return this;
    }

    public CocoQuery image(String url, Drawable holder,int fallbackId) {
        Picasso.with(getContext()).load(url).error(fallbackId).placeholder(holder).into(getImageView());
        return this;
    }

    public CocoQuery image(String url, Drawable holder,Drawable fallbackId) {
        Picasso.with(getContext()).load(url).error(fallbackId).placeholder(holder).into(getImageView());
        return this;
    }

    public final <E extends View> E view () {
            return (E) getView();
    }


    @Deprecated
    @Override
    public CocoQuery image(String url, boolean memCache, boolean fileCache, int targetWidth, int fallbackId) {
        return this;
    }

    @Deprecated
    @Override
    public CocoQuery image(String url, boolean memCache, boolean fileCache, int targetWidth, int fallbackId, Bitmap preset, int animId) {
        return super.image(url, memCache, fileCache, targetWidth, fallbackId, preset, animId);
    }

    @Deprecated
    @Override
    public CocoQuery image(String url, boolean memCache, boolean fileCache, int targetWidth, int fallbackId, Bitmap preset, int animId, float ratio) {
        return super.image(url, memCache, fileCache, targetWidth, fallbackId, preset, animId, ratio);
    }

    @Deprecated
    @Override
    protected CocoQuery image(String url, boolean memCache, boolean fileCache, int targetWidth, int fallbackId, Bitmap preset, int animId, float ratio, int round, String networkUrl) {
        return super.image(url, memCache, fileCache, targetWidth, fallbackId, preset, animId, ratio, round, networkUrl);
    }

    @Deprecated
    @Override
    public CocoQuery image(String url, ImageOptions options) {
        return super.image(url, options);
    }

    @Deprecated
    @Override
    protected CocoQuery image(String url, ImageOptions options, String networkUrl) {
        return super.image(url, options, networkUrl);
    }

    @Deprecated
    @Override
    public CocoQuery image(BitmapAjaxCallback callback) {
        return super.image(callback);
    }

    @Deprecated
    @Override
    public CocoQuery image(String url, boolean memCache, boolean fileCache, int targetWidth, int resId, BitmapAjaxCallback callback) {
        return super.image(url, memCache, fileCache, targetWidth, resId, callback);
    }

    @Deprecated
    @Override
    public CocoQuery image(File file, int targetWidth) {
        return super.image(file, targetWidth);
    }

    @Deprecated
    @Override
    public CocoQuery image(File file, boolean memCache, int targetWidth, BitmapAjaxCallback callback) {
        return super.image(file, memCache, targetWidth, callback);
    }

    @Deprecated
    @Override
    public CocoQuery image(Bitmap bm, float ratio) {
        return super.image(bm, ratio);
    }
}