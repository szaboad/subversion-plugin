/*
 * The MIT License
 * 
 * Copyright (c) 2004-2009, Sun Microsystems, Inc., Kohsuke Kawaguchi, Daniel Dyer
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package hudson.scm.browsers;

import hudson.model.Descriptor;

import hudson.scm.EditType;
import hudson.scm.RepositoryBrowser;
import hudson.scm.SubversionChangeLogSet;

import hudson.scm.SubversionChangeLogSet.Path;

import hudson.scm.SubversionRepositoryBrowser;
import hudson.Extension;

import org.kohsuke.stapler.DataBoundConstructor;

import java.io.IOException;

import java.net.MalformedURLException;
import java.net.URL;


/**
 * Phabricator {@link RepositoryBrowser} for Subversion.
 *
 * @author Adam Szabo
 */
public final class Phabricator extends SubversionRepositoryBrowser {

    @Extension
    public static class DescriptorImpl extends Descriptor<RepositoryBrowser<?>> {
        public String getDisplayName() {
            return "Phabricator";
        }
    }

    private static final long serialVersionUID = 1L;

    /**
     * The URL of the top of the site.
     * <p>Normalized to end with '/', like <tt>http://secure.phabricator.com/</tt>
     */
    public final URL url;

    /**
     * Callsign of the repository
     * See https://secure.phabricator.com/book/phabricator/article/diffusion/#repository-callsigns-and
     */
    private final String callsign;

    @DataBoundConstructor
    public Phabricator(URL url, String callsign) throws MalformedURLException {
        this.url = normalizeToEndWithSlash(url);
        this.callsign = callsign;
    }

    @Override
    public URL getDiffLink(Path path) throws IOException {
        if (path.getEditType() != EditType.EDIT) {
            return null; // no diff if this is not an edit change
        }
        return new URL(url, "diffusion/" + callsign + "/change/" + trimHeadSlash(path.getValue()) + ";" + path.getLogEntry().getRevision());
    }

    @Override
    public URL getFileLink(Path path) throws IOException {
        return new URL(url, "diffusion/" + callsign + "/browse/" + trimHeadSlash(path.getValue()) + ";" + path.getLogEntry().getRevision());
    }

    @Override
    public URL getChangeSetLink(SubversionChangeLogSet.LogEntry changeSet) throws IOException {
        return new URL(url, "r" + callsign + changeSet.getRevision());
    }

    public String getCallsign() {
        return callsign;
    }
}