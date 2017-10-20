/*
 * Copyright 2017 dmfs GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.dmfs.tasks.data;

import android.content.ContentProviderClient;
import android.content.Context;
import android.net.Uri;

import org.dmfs.android.contentpal.RowSet;
import org.dmfs.android.contentpal.rowsets.Cached;
import org.dmfs.opentaskspal.rowsets.Subtasks;
import org.dmfs.tasks.contract.TaskContract.Tasks;

import gk.android.investigator.Investigator;
import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.SingleSource;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;

import static org.dmfs.provider.tasks.AuthorityUtil.taskAuthority;


/**
 * @author Gabor Keszthelyi
 */
public final class SubtasksSource implements SingleSource<RowSet<Tasks>>
{
    private final Context mAppContext;
    private final Uri mTaskUri;


    public SubtasksSource(Context appContext, Uri taskUri)
    {
        mAppContext = appContext;
        mTaskUri = taskUri;
    }


    @Override
    public void subscribe(@NonNull SingleObserver<? super RowSet<Tasks>> observer)
    {
        Single.wrap(new ContentProviderClientSource(mAppContext, mTaskUri))
                .map(new Function<ContentProviderClient, RowSet<Tasks>>()
                {
                    @Override
                    public RowSet<Tasks> apply(@NonNull ContentProviderClient client) throws Exception
                    {
                        Investigator.log(this, "subtask query and titles extraction");

                        RowSet<Tasks> frozen = new Cached<>(new Subtasks(taskAuthority(mAppContext), client, mTaskUri));
                        frozen.iterator(); // To actually freeze it
                        return frozen;
                    }
                })
                .subscribe(observer);
    }

}
