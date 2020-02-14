/*
 * Copyright (c) 2017 Guus der Kinderen. All rights reserved.
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
 *
 */

package org.igniterealtime.openfire.plugins.httpfileupload;

import org.xmpp.packet.JID;

/**
 * A manager of HTTP slots.
 *
 * @author Guus der Kinderen, guus@goodbytes.nl
 */
// TODO: quick 'n dirty singleton. Is this the best choice?
// TODO: persist internal state to allow for restart survival.
public class SlotManager
{
    public static final long DEFAULT_MAX_FILE_SIZE = 50 * 1024 * 1024;
    private static SlotManager INSTANCE = null;
    private long maxFileSize = DEFAULT_MAX_FILE_SIZE;
    public static final int DEFAULT_SLOT_TIMEOUT = 15000;
    public static final String DEFAULT_UPLOAD_SERVICE_HOST = "voice-service";
    private String uploadServicePath;

    private int slotCreationTimeout = DEFAULT_SLOT_TIMEOUT;
    private SlotService slotService;

    private SlotManager()
    {

    }

    public synchronized static SlotManager getInstance()
    {
        if ( INSTANCE == null )
        {
            INSTANCE = new SlotManager();
        }

        return INSTANCE;
    }

    public long getMaxFileSize()
    {
        return maxFileSize;
    }

    public void setMaxFileSize( long maxFileSize )
    {
        this.maxFileSize = maxFileSize;
    }

    public Slot getSlot(JID from, String fileName, long fileSize ) throws TooLargeException
    {
        if ( maxFileSize > 0 && fileSize > maxFileSize )
        {
            throw new TooLargeException( fileSize, maxFileSize );
        }

        slotService = new VoiceSlotService();
        slotService.setUploadServiceHost(getUploadServicePath());
        slotService.setSlotCreationTimeout(getSlotCreationTimeout());
        return slotService.createSlot(from.toBareJID(), fileName);
    }

    public String getUploadServicePath()
    {
        return uploadServicePath;
    }

    public void setUploadServicePath( final String uploadServicePath )
    {
        this.uploadServicePath = uploadServicePath;
    }

    public int getSlotCreationTimeout() {
        return slotCreationTimeout;
    }

    public void setSlotCreationTimeout(int slotCreationTimeout) {
        this.slotCreationTimeout = slotCreationTimeout;
    }
}
