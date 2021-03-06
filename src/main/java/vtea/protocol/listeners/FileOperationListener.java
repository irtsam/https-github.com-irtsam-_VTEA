package vtea.protocol.listeners;

/*
 * Copyright (C) 2017 SciJava
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

/**
 *
 * @author sethwinfree
 */
public interface FileOperationListener {
    
	/**
	 * Notifies listeners to attempt to open a file of the appropriate type.  Exceptions must be handled by
	 * the caller
	 * 
	 * @throws Exception
	 */
	public int onFileOpen() throws Exception;
	/**
	 * Notifies listeners to attempt to save a file of the appropriate type.  Exceptions must be handled by
	 * the caller
	 * 
	 * @throws Exception
	 */
	public void onFileSave() throws Exception;
	/**
	 * Notifies listeners to attempt to export a file of the appropriate type.  Exceptions must be handled by
	 * the caller
	 * 
	 * @throws Exception
	 */
	public void onFileExport() throws Exception;
	
}

