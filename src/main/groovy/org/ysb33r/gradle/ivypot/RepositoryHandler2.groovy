//package org.ysb33r.gradle.ivypot
//
//import groovy.transform.CompileStatic
//import org.gradle.api.artifacts.repositories.ArtifactRepository
//import org.gradle.api.artifacts.repositories.MavenArtifactRepository
//import org.ysb33r.gradle.ivypot.internal.MavenCentral
//
///** Implements a specialist repository to be used for offline repository synchrosiation.
// * @author Schalk W. Cronjé
// */
//@CompileStatic
//class RepositoryHandler2 implements Iterable<ArtifactRepository> {
//
////    /**
////     * Adds a resolver that looks into a number of directories for artifacts. The artifacts are expected to be located in the
////     * root of the specified directories. The resolver ignores any group/organization information specified in the
////     * dependency section of your build script. If you only use this kind of resolver you might specify your
////     * dependencies like <code>":junit:4.4"</code> instead of <code>"junit:junit:4.4"</code>.
////     *
////     * The following parameter are accepted as keys for the map:
////     *
////     * <table summary="Shows property keys and associated values">
////     * <tr><th>Key</th>
////     *     <th>Description of Associated Value</th></tr>
////     * <tr><td><code>name</code></td>
////     *     <td><em>(optional)</em> The name of the repository.
////     * The default is a Hash value of the rootdir paths. The name is used in the console output,
////     * to point to information related to a particular repository. A name must be unique amongst a repository group.</td></tr>
////     * <tr><td><code>dirs</code></td>
////     *     <td>Specifies a list of rootDirs where to look for dependencies. These are evaluated as per {@link org.gradle.api.Project#files(Object ...)}</td></tr>
////     * </table>
////     *
////     * <p>Examples:
////     * <pre autoTested=''>
////     * repositories {*     flatDir name: 'libs', dirs: "$projectDir/libs"
////     *     flatDir dirs: ["$projectDir/libs1", "$projectDir/libs2"]
////     *}* </pre>
////     * </p>
////     *
////     * @param args The arguments used to configure the repository.
////     * @return the added resolver
////     * @throws org.gradle.api.InvalidUserDataException In the case neither rootDir nor rootDirs is specified of if both
////     * are specified.
////     */
////    @Override
////    FlatDirectoryArtifactRepository flatDir(Map<String, ?> args) {
////        return null
////    }
////
////    /**
////     * Adds an configures a repository which will look for dependencies in a number of local directories.
////     *
////     * @param configureClosure The closure to execute to configure the repository.
////     * @return The repository.
////     */
////    @Override
////    FlatDirectoryArtifactRepository flatDir(Closure configureClosure) {
////        return null
////    }
////
////    /**
////     * Adds an configures a repository which will look for dependencies in a number of local directories.
////     *
////     * @param action The action to execute to configure the repository.
////     * @return The repository.
////     */
////    @Override
////    FlatDirectoryArtifactRepository flatDir(Action<? super FlatDirectoryArtifactRepository> action) {
////        return null
////    }
////
////    /**
////     * Adds a repository which looks in Bintray's JCenter repository for dependencies.
////     * <p>
////     * The URL used to access this repository is {@literal "http://jcenter.bintray.com/"}.
////     * The behavior of this resolver is otherwise the same as the ones added by {@link #maven(org.gradle.api.Action)}.
////     * <p>
////     * Examples:
////     * <pre autoTested="">
////     * repositories {*   jcenter {*     artifactUrls = ["http://www.mycompany.com/artifacts1", "http://www.mycompany.com/artifacts2"]
////     *}*   jcenter {*     name = "nonDefaultName"
////     *     artifactUrls = ["http://www.mycompany.com/artifacts1"]
////     *}*}* </pre>
////     *
////     * @param action a configuration action
////     * @return the added repository
////     */
////    @Override
////    MavenArtifactRepository jcenter(Action<? super MavenArtifactRepository> action) {
////        return null
////    }
////
////    /**
////     * Adds a repository which looks in Bintray's JCenter repository for dependencies.
////     * <p>
////     * The URL used to access this repository is {@literal "http://jcenter.bintray.com/"}.
////     * The behavior of this resolver is otherwise the same as the ones added by {@link #mavenCentral()}.
////     * <p>
////     * Examples:
////     * <pre autoTested="">
////     * repositories {*     jcenter()
////     *}* </pre>
////     *
////     * @return the added resolver
////     * @see #jcenter(Action)
////     */
////    @Override
////    MavenArtifactRepository jcenter() {
////        return null
////    }
////
//    /**
//     * Adds a repository which looks in the Maven central repository for dependencies. The URL used to access this repository is
//     * {@value org.gradle.api.artifacts.ArtifactRepositoryContainer#MAVEN_CENTRAL_URL}.
//     *
//     * The following parameter are accepted as keys for the map:
//     *
//     * <table summary="Shows property keys and associated values">
//     * <tr><th>Key</th>
//     *     <th>Description of Associated Value</th></tr>
//     * <tr><td><code>name</code></td>
//     *     <td><em>(optional)</em> The name of the repository. The default is
//     * {@value org.gradle.api.artifacts.ArtifactRepositoryContainer#DEFAULT_MAVEN_CENTRAL_REPO_NAME} is used as the name. A name
//     * must be unique amongst a repository group.
//     * </td></tr>
//     * <tr><td><code>artifactUrls</code></td>
//     *     <td>A single jar repository or a collection of jar repositories containing additional artifacts not found in the Maven central repository.
//     * But be aware that the POM must exist in Maven central.
//     * The provided values are evaluated as per {@link org.gradle.api.Project#uri(Object)}.</td></tr>
//     * </table>
//     *
//     * <p>Examples:
//     * <pre autoTested="">
//     * repositories {*     mavenCentral artifactUrls: ["http://www.mycompany.com/artifacts1", "http://www.mycompany.com/artifacts2"]
//     *     mavenCentral name: "nonDefaultName", artifactUrls: ["http://www.mycompany.com/artifacts1"]
//     *}* </pre>
//     * </p>
//     *
//     * @param args A list of urls of repositories to look for artifacts only. Ignored. Implemented for compatiblility with interface
//     * only
//     * @return the added repository
//     */
////    @Override
//    MavenArtifactRepository mavenCentral(Map<String, ?> args) {
//        do_add(new MavenCentral(args))  as MavenArtifactRepository
//    }
//
//    /**
//     * Adds a repository which looks in the Maven central repository for dependencies. The URL used to access this repository is
//     * {@value org.gradle.api.artifacts.ArtifactRepositoryContainer#MAVEN_CENTRAL_URL}. The name of the repository is
//     * {@value org.gradle.api.artifacts.ArtifactRepositoryContainer#DEFAULT_MAVEN_CENTRAL_REPO_NAME}.
//     *
//     * <p>Examples:
//     * <pre autoTested="">
//     * repositories {*     mavenCentral()
//     *}* </pre>
//     * </p>
//     *
//     * @return the added resolver
//     * @see #mavenCentral(java.util.Map)
//     */
////    @Override
//    MavenArtifactRepository mavenCentral() {
//        do_add(new MavenCentral()) as MavenArtifactRepository
//    }
////
////    /**
////     * Adds a repository which looks in the local Maven cache for dependencies. The name of the repository is
////     * {@value org.gradle.api.artifacts.ArtifactRepositoryContainer#DEFAULT_MAVEN_LOCAL_REPO_NAME}.
////     *
////     * <p>Examples:
////     * <pre autoTested="">
////     * repositories {*     mavenLocal()
////     *}* </pre>
////     * </p>
////     *
////     * @return the added resolver
////     */
////    @Override
////    MavenArtifactRepository mavenLocal() {
////        return null
////    }
////
////    /**
////     * Adds and configures a Maven repository.
////     *
////     * @param closure The closure to use to configure the repository.
////     * @return The added repository.
////     */
////    @Override
////    MavenArtifactRepository maven(Closure closure) {
////        return null
////    }
////
////    /**
////     * Adds and configures a Maven repository.
////     *
////     * @param action The action to use to configure the repository.
////     * @return The added repository.
////     */
////    @Override
////    MavenArtifactRepository maven(Action<? super MavenArtifactRepository> action) {
////        return null
////    }
////
////    /**
////     * Adds and configures an Ivy repository.
////     *
////     * @param closure The closure to use to configure the repository.
////     * @return The added repository.
////     */
////    @Override
////    IvyArtifactRepository ivy(Closure closure) {
////        return null
////    }
////
////    /**
////     * Adds and configures an Ivy repository.
////     *
////     * @param action The action to use to configure the repository.
////     * @return The added repository.
////     */
////    @Override
////    IvyArtifactRepository ivy(Action<? super IvyArtifactRepository> action) {
////        return null
////    }
////
////    /**
////     * Adds a repository to this container, at the end of the repository sequence.
////     *
////     * @param repository The repository to add.
////     */
////    /**
////     * Returns the number of elements in this collection.  If this collection
////     * contains more than <tt>Integer.MAX_VALUE</tt> elements, returns
////     * <tt>Integer.MAX_VALUE</tt>.
////     *
////     * @return the number of elements in this collection
////     */
////    @Override
////    int size() {
////        return 0
////    }
////
////    /**
////     * Returns <tt>true</tt> if this collection contains no elements.
////     *
////     * @return <tt>true</tt> if this collection contains no elements
////     */
////    @Override
////    boolean isEmpty() {
////        return false
////    }
////
////    /**
////     * Returns <tt>true</tt> if this collection contains the specified element.
////     * More formally, returns <tt>true</tt> if and only if this collection
////     * contains at least one element <tt>e</tt> such that
////     * <tt>(o==null&nbsp;?&nbsp;e==null&nbsp;:&nbsp;o.equals(e))</tt>.
////     *
////     * @param o element whose presence in this collection is to be tested
////     * @return <tt>true</tt> if this collection contains the specified
////     *         element
////     * @throws ClassCastException if the type of the specified element
////     *         is incompatible with this collection
////     *         (<a href="#optional-restrictions">optional</a>)
////     * @throws NullPointerException if the specified element is null and this
////     *         collection does not permit null elements
////     *         (<a href="#optional-restrictions">optional</a>)
////     */
////    @Override
////    boolean contains(Object o) {
////        return false
////    }
////
//    /**
//     * Returns an iterator over the elements in this collection.  There are no
//     * guarantees concerning the order in which the elements are returned
//     * (unless this collection is an instance of some class that provides a
//     * guarantee).
//     *
//     * @return an <tt>Iterator</tt> over the elements in this collection
//     */
////    @Override
//    Iterator<ArtifactRepository> iterator() {
//        return repositories.iterator()
//    }
//
////    /**
////     * Returns an array containing all of the elements in this collection.
////     * If this collection makes any guarantees as to what order its elements
////     * are returned by its iterator, this method must return the elements in
////     * the same order.
////     *
////     * <p>The returned array will be "safe" in that no references to it are
////     * maintained by this collection.  (In other words, this method must
////     * allocate a new array even if this collection is backed by an array).
////     * The caller is thus free to modify the returned array.
////     *
////     * <p>This method acts as bridge between array-based and collection-based
////     * APIs.
////     *
////     * @return an array containing all of the elements in this collection
////     */
////    @Override
////    Object[] toArray() {
////        return new Object[0]
////    }
////
////    /**
////     * Returns an array containing all of the elements in this collection;
////     * the runtime type of the returned array is that of the specified array.
////     * If the collection fits in the specified array, it is returned therein.
////     * Otherwise, a new array is allocated with the runtime type of the
////     * specified array and the size of this collection.
////     *
////     * <p>If this collection fits in the specified array with room to spare
////     * (i.e., the array has more elements than this collection), the element
////     * in the array immediately following the end of the collection is set to
////     * <tt>null</tt>.  (This is useful in determining the length of this
////     * collection <i>only</i> if the caller knows that this collection does
////     * not contain any <tt>null</tt> elements.)
////     *
////     * <p>If this collection makes any guarantees as to what order its elements
////     * are returned by its iterator, this method must return the elements in
////     * the same order.
////     *
////     * <p>Like the {@link #toArray()} method, this method acts as bridge between
////     * array-based and collection-based APIs.  Further, this method allows
////     * precise control over the runtime type of the output array, and may,
////     * under certain circumstances, be used to save allocation costs.
////     *
////     * <p>Suppose <tt>x</tt> is a collection known to contain only strings.
////     * The following code can be used to dump the collection into a newly
////     * allocated array of <tt>String</tt>:
////     *
////     * <pre>
////     *     String[] y = x.toArray(new String[0]);</pre>
////     *
////     * Note that <tt>toArray(new Object[0])</tt> is identical in function to
////     * <tt>toArray()</tt>.
////     *
////     * @param a the array into which the elements of this collection are to be
////     *        stored, if it is big enough; otherwise, a new array of the same
////     *        runtime type is allocated for this purpose.
////     * @return an array containing all of the elements in this collection
////     * @throws ArrayStoreException if the runtime type of the specified array
////     *         is not a supertype of the runtime type of every element in
////     *         this collection
////     * @throws NullPointerException if the specified array is null
////     */
////    @Override
////    def <T> T[] toArray(T[] a) {
////        return null
////    }
////
////    @Override
//    boolean add(ArtifactRepository repository) {
//        return false
//    }
////
////    /**
////     * Removes a single instance of the specified element from this
////     * collection, if it is present (optional operation).  More formally,
////     * removes an element <tt>e</tt> such that
////     * <tt>(o==null&nbsp;?&nbsp;e==null&nbsp;:&nbsp;o.equals(e))</tt>, if
////     * this collection contains one or more such elements.  Returns
////     * <tt>true</tt> if this collection contained the specified element (or
////     * equivalently, if this collection changed as a result of the call).
////     *
////     * @param o element to be removed from this collection, if present
////     * @return <tt>true</tt> if an element was removed as a result of this call
////     * @throws ClassCastException if the type of the specified element
////     *         is incompatible with this collection
////     *         (<a href="#optional-restrictions">optional</a>)
////     * @throws NullPointerException if the specified element is null and this
////     *         collection does not permit null elements
////     *         (<a href="#optional-restrictions">optional</a>)
////     * @throws UnsupportedOperationException if the <tt>remove</tt> operation
////     *         is not supported by this collection
////     */
////    @Override
////    boolean remove(Object o) {
////        return false
////    }
////
////    /**
////     * Returns <tt>true</tt> if this collection contains all of the elements
////     * in the specified collection.
////     *
////     * @param c collection to be checked for containment in this collection
////     * @return <tt>true</tt> if this collection contains all of the elements
////     *         in the specified collection
////     * @throws ClassCastException if the types of one or more elements
////     *         in the specified collection are incompatible with this
////     *         collection
////     *         (<a href="#optional-restrictions">optional</a>)
////     * @throws NullPointerException if the specified collection contains one
////     *         or more null elements and this collection does not permit null
////     *         elements
////     *         (<a href="#optional-restrictions">optional</a>),
////     *         or if the specified collection is null.
////     * @see #contains(Object)
////     */
////    @Override
////    boolean containsAll(Collection<?> c) {
////        return false
////    }
////
////    /**
////     * Adds any of the given objects to the collection that do not have the same name as any existing element.
////     *
////     * @param c the items to add to the collection
////     * @return {@code true} if any item was added, or {@code} false if all items have non unique names within this collection.
////     */
////    @Override
////    boolean addAll(Collection<? extends ArtifactRepository> c) {
////        return false
////    }
////
////    /**
////     * Removes all of this collection's elements that are also contained in the
////     * specified collection (optional operation).  After this call returns,
////     * this collection will contain no elements in common with the specified
////     * collection.
////     *
////     * @param c collection containing elements to be removed from this collection
////     * @return <tt>true</tt> if this collection changed as a result of the
////     *         call
////     * @throws UnsupportedOperationException if the <tt>removeAll</tt> method
////     *         is not supported by this collection
////     * @throws ClassCastException if the types of one or more elements
////     *         in this collection are incompatible with the specified
////     *         collection
////     *         (<a href="#optional-restrictions">optional</a>)
////     * @throws NullPointerException if this collection contains one or more
////     *         null elements and the specified collection does not support
////     *         null elements
////     *         (<a href="#optional-restrictions">optional</a>),
////     *         or if the specified collection is null
////     * @see #remove(Object)
////     * @see #contains(Object)
////     */
////    @Override
////    boolean removeAll(Collection<?> c) {
////        return false
////    }
////
////    /**
////     * Retains only the elements in this collection that are contained in the
////     * specified collection (optional operation).  In other words, removes from
////     * this collection all of its elements that are not contained in the
////     * specified collection.
////     *
////     * @param c collection containing elements to be retained in this collection
////     * @return <tt>true</tt> if this collection changed as a result of the call
////     * @throws UnsupportedOperationException if the <tt>retainAll</tt> operation
////     *         is not supported by this collection
////     * @throws ClassCastException if the types of one or more elements
////     *         in this collection are incompatible with the specified
////     *         collection
////     *         (<a href="#optional-restrictions">optional</a>)
////     * @throws NullPointerException if this collection contains one or more
////     *         null elements and the specified collection does not permit null
////     *         elements
////     *         (<a href="#optional-restrictions">optional</a>),
////     *         or if the specified collection is null
////     * @see #remove(Object)
////     * @see #contains(Object)
////     */
////    @Override
////    boolean retainAll(Collection<?> c) {
////        return false
////    }
////
////    /**
////     * Removes all of the elements from this collection (optional operation).
////     * The collection will be empty after this method returns.
////     *
////     * @throws UnsupportedOperationException if the <tt>clear</tt> operation
////     *         is not supported by this collection
////     */
////    @Override
////    void clear() {
////
////    }
////
////    /**
////     * An object that represents the naming strategy used to name objects of this collection.
////     *
////     * @return Object representing the naming strategy.
////     */
////    @Override
////    Namer<ArtifactRepository> getNamer() {
////        return null
////    }
////
////    /**
////     * <p>Returns the objects in this collection, as a map from object name to object instance.</p>
////     *
////     * <p>The map is ordered by the <em>natural ordering</em> of the object names (i.e. keys).</p>
////     *
////     * @return The objects. Returns an empty map if this collection is empty.
////     */
////    @Override
////    SortedMap<String, ArtifactRepository> getAsMap() {
////        return null
////    }
////
////    /**
////     * <p>Returns the names of the objects in this collection as a Set of Strings.</p>
////     *
////     * <p>The set of names is in <em>natural ordering</em>.</p>
////     *
////     * @return The names. Returns an empty set if this collection is empty.
////     */
////    @Override
////    SortedSet<String> getNames() {
////        return null
////    }
////
////    /**
////     * Locates an object by name, returning null if there is no such object.
////     *
////     * @param name The object name
////     * @return The object with the given name, or null if there is no such object in this collection.
////     */
////    @Override
////    ArtifactRepository findByName(String name) {
////        return null
////    }
////
////    /**
////     * Inserts all of the elements in the specified collection into this
////     * list at the specified position (optional operation).  Shifts the
////     * element currently at that position (if any) and any subsequent
////     * elements to the right (increases their indices).  The new elements
////     * will appear in this list in the order that they are returned by the
////     * specified collection's iterator.  The behavior of this operation is
////     * undefined if the specified collection is modified while the
////     * operation is in progress.  (Note that this will occur if the specified
////     * collection is this list, and it's nonempty.)
////     *
////     * @param index index at which to insert the first element from the
////     *              specified collection
////     * @param c collection containing elements to be added to this list
////     * @return <tt>true</tt> if this list changed as a result of the call
////     * @throws UnsupportedOperationException if the <tt>addAll</tt> operation
////     *         is not supported by this list
////     * @throws ClassCastException if the class of an element of the specified
////     *         collection prevents it from being added to this list
////     * @throws NullPointerException if the specified collection contains one
////     *         or more null elements and this list does not permit null
////     *         elements, or if the specified collection is null
////     * @throws IllegalArgumentException if some property of an element of the
////     *         specified collection prevents it from being added to this list
////     * @throws IndexOutOfBoundsException if the index is out of range
////     *         (<tt>index &lt; 0 || index &gt; size()</tt>)
////     */
////    @Override
////    boolean addAll(int index, Collection<? extends ArtifactRepository> c) {
////        return false
////    }
////
////    /**
////     * Returns the element at the specified position in this list.
////     *
////     * @param index index of the element to return
////     * @return the element at the specified position in this list
////     * @throws IndexOutOfBoundsException if the index is out of range
////     *         (<tt>index &lt; 0 || index &gt;= size()</tt>)
////     */
////    @Override
////    ArtifactRepository get(int index) {
////        return null
////    }
////
////    /**
////     * Replaces the element at the specified position in this list with the
////     * specified element (optional operation).
////     *
////     * @param index index of the element to replace
////     * @param element element to be stored at the specified position
////     * @return the element previously at the specified position
////     * @throws UnsupportedOperationException if the <tt>set</tt> operation
////     *         is not supported by this list
////     * @throws ClassCastException if the class of the specified element
////     *         prevents it from being added to this list
////     * @throws NullPointerException if the specified element is null and
////     *         this list does not permit null elements
////     * @throws IllegalArgumentException if some property of the specified
////     *         element prevents it from being added to this list
////     * @throws IndexOutOfBoundsException if the index is out of range
////     *         (<tt>index &lt; 0 || index &gt;= size()</tt>)
////     */
////    @Override
////    ArtifactRepository set(int index, ArtifactRepository element) {
////        return null
////    }
////
////    /**
////     * Inserts the specified element at the specified position in this list
////     * (optional operation).  Shifts the element currently at that position
////     * (if any) and any subsequent elements to the right (adds one to their
////     * indices).
////     *
////     * @param index index at which the specified element is to be inserted
////     * @param element element to be inserted
////     * @throws UnsupportedOperationException if the <tt>add</tt> operation
////     *         is not supported by this list
////     * @throws ClassCastException if the class of the specified element
////     *         prevents it from being added to this list
////     * @throws NullPointerException if the specified element is null and
////     *         this list does not permit null elements
////     * @throws IllegalArgumentException if some property of the specified
////     *         element prevents it from being added to this list
////     * @throws IndexOutOfBoundsException if the index is out of range
////     *         (<tt>index &lt; 0 || index &gt; size()</tt>)
////     */
////    @Override
////    void add(int index, ArtifactRepository element) {
////
////    }
////
////    /**
////     * Removes the element at the specified position in this list (optional
////     * operation).  Shifts any subsequent elements to the left (subtracts one
////     * from their indices).  Returns the element that was removed from the
////     * list.
////     *
////     * @param index the index of the element to be removed
////     * @return the element previously at the specified position
////     * @throws UnsupportedOperationException if the <tt>remove</tt> operation
////     *         is not supported by this list
////     * @throws IndexOutOfBoundsException if the index is out of range
////     *         (<tt>index &lt; 0 || index &gt;= size()</tt>)
////     */
////    @Override
////    ArtifactRepository remove(int index) {
////        return null
////    }
////
////    /**
////     * Returns the index of the first occurrence of the specified element
////     * in this list, or -1 if this list does not contain the element.
////     * More formally, returns the lowest index <tt>i</tt> such that
////     * <tt>(o==null&nbsp;?&nbsp;get(i)==null&nbsp;:&nbsp;o.equals(get(i)))</tt>,
////     * or -1 if there is no such index.
////     *
////     * @param o element to search for
////     * @return the index of the first occurrence of the specified element in
////     *         this list, or -1 if this list does not contain the element
////     * @throws ClassCastException if the type of the specified element
////     *         is incompatible with this list
////     *         (<a href="Collection.html#optional-restrictions">optional</a>)
////     * @throws NullPointerException if the specified element is null and this
////     *         list does not permit null elements
////     *         (<a href="Collection.html#optional-restrictions">optional</a>)
////     */
////    @Override
////    int indexOf(Object o) {
////        return 0
////    }
////
////    /**
////     * Returns the index of the last occurrence of the specified element
////     * in this list, or -1 if this list does not contain the element.
////     * More formally, returns the highest index <tt>i</tt> such that
////     * <tt>(o==null&nbsp;?&nbsp;get(i)==null&nbsp;:&nbsp;o.equals(get(i)))</tt>,
////     * or -1 if there is no such index.
////     *
////     * @param o element to search for
////     * @return the index of the last occurrence of the specified element in
////     *         this list, or -1 if this list does not contain the element
////     * @throws ClassCastException if the type of the specified element
////     *         is incompatible with this list
////     *         (<a href="Collection.html#optional-restrictions">optional</a>)
////     * @throws NullPointerException if the specified element is null and this
////     *         list does not permit null elements
////     *         (<a href="Collection.html#optional-restrictions">optional</a>)
////     */
////    @Override
////    int lastIndexOf(Object o) {
////        return 0
////    }
////
////    /**
////     * Returns a list iterator over the elements in this list (in proper
////     * sequence).
////     *
////     * @return a list iterator over the elements in this list (in proper
////     *         sequence)
////     */
////    @Override
////    ListIterator<ArtifactRepository> listIterator() {
////        return null
////    }
////
////    /**
////     * Returns a list iterator over the elements in this list (in proper
////     * sequence), starting at the specified position in the list.
////     * The specified index indicates the first element that would be
////     * returned by an initial call to {@link ListIterator#next next}.
////     * An initial call to {@link ListIterator#previous previous} would
////     * return the element with the specified index minus one.
////     *
////     * @param index index of the first element to be returned from the
////     *        list iterator (by a call to {@link ListIterator#next next})
////     * @return a list iterator over the elements in this list (in proper
////     *         sequence), starting at the specified position in the list
////     * @throws IndexOutOfBoundsException if the index is out of range
////     *         ({@code index < 0 || index > size()})
////     */
////    @Override
////    ListIterator<ArtifactRepository> listIterator(int index) {
////        return null
////    }
////
////    /**
////     * Returns a view of the portion of this list between the specified
////     * <tt>fromIndex</tt>, inclusive, and <tt>toIndex</tt>, exclusive.  (If
////     * <tt>fromIndex</tt> and <tt>toIndex</tt> are equal, the returned list is
////     * empty.)  The returned list is backed by this list, so non-structural
////     * changes in the returned list are reflected in this list, and vice-versa.
////     * The returned list supports all of the optional list operations supported
////     * by this list.<p>
////     *
////     * This method eliminates the need for explicit range operations (of
////     * the sort that commonly exist for arrays).  Any operation that expects
////     * a list can be used as a range operation by passing a subList view
////     * instead of a whole list.  For example, the following idiom
////     * removes a range of elements from a list:
////     * <pre>{@code
////     * list.subList ( from , to ) .clear ( ) ;
////     *}</pre>
////     * Similar idioms may be constructed for <tt>indexOf</tt> and
////     * <tt>lastIndexOf</tt>, and all of the algorithms in the
////     * <tt>Collections</tt> class can be applied to a subList.<p>
////     *
////     * The semantics of the list returned by this method become undefined if
////     * the backing list (i.e., this list) is <i>structurally modified</i> in
////     * any way other than via the returned list.  (Structural modifications are
////     * those that change the size of this list, or otherwise perturb it in such
////     * a fashion that iterations in progress may yield incorrect results.)
////     *
////     * @param fromIndex low endpoint (inclusive) of the subList
////     * @param toIndex high endpoint (exclusive) of the subList
////     * @return a view of the specified range within this list
////     * @throws IndexOutOfBoundsException for an illegal endpoint index value
////     *         (<tt>fromIndex &lt; 0 || toIndex &gt; size ||
////     *         fromIndex &gt; toIndex</tt>)
////     */
////    @Override
////    List<ArtifactRepository> subList(int fromIndex, int toIndex) {
////        return null
////    }
/////**
////     * Adds a repository to this container, at the start of the repository sequence.
////     *
////     * @param repository The repository to add.
////     */
////    @Override
////    void addFirst(ArtifactRepository repository) {
////
////    }
////
////    /**
////     * Adds a repository to this container, at the end of the repository sequence.
////     *
////     * @param repository The repository to add.
////     */
////    @Override
////    void addLast(ArtifactRepository repository) {
////
////    }
////
////    /**
////     * {@inheritDoc}
////     */
////    @Override
////    ArtifactRepository getByName(String name) throws UnknownRepositoryException {
////        return null
////    }
////
////    /**
////     * @param name
////     * @param configureClosure
////    */
////    @Override
////    ArtifactRepository getByName(String name, Closure configureClosure) throws UnknownRepositoryException {
////        return null
////    }
////
////    /**
////     * {@inheritDoc}
////     */
////    @Override
////    ArtifactRepository getAt(String name) throws UnknownRepositoryException {
////        return null
////    }
////
////    /**
////     * Adds a rule to this collection. The given rule is invoked when an unknown object is requested by name.
////     *
////     * @param rule The rule to add.
////     * @return The added rule.
////     */
////    @Override
////    Rule addRule(Rule rule) {
////        return null
////    }
////
////    /**
////     * Adds a rule to this collection. The given closure is executed when an unknown object is requested by name. The
////     * requested name is passed to the closure as a parameter.
////     *
////     * @param description The description of the rule.
////     * @param ruleAction The closure to execute to apply the rule.
////     * @return The added rule.
////     */
////    @Override
////    Rule addRule(String description, Closure ruleAction) {
////        return null
////    }
////
////    /**
////     * Returns the rules used by this collection.
////     *
////     * @return The rules, in the order they will be applied.
////     */
////    @Override
////    List<Rule> getRules() {
////        return null
////    }
/////** Returns an Ivy configuration {@code <resolvers>} block that can be used to configure a set of
////     * resolvers.
////     *
////     * @return XML string
////     */
////    String ivyXml() {
////
////        // foreach of our repos
//////    <resolvers>
//////        <bintray name="${defaultResolver}"/>
//////        <filesystem name="${LOCALREPONAME}">
//////            <ivy pattern="${repoRoot}/${IVY_PATTERN}"/>
//////            <artifact pattern="${repoRoot}/${ARTIFACT_PATTERN}"/>
//////        </filesystem>
//////	</resolvers>
////    }
////
////    @Override
////    ArtifactRepositoryContainer configure(Closure cl) {
////        return null
////    }
////
////    /**
////     * {@inheritDoc}
////     */
////    @Override
////    def <S extends T> NamedDomainObjectList<S> withType(Class<S> type) {
////        return null
////    }
////
////    /**
////     * {@inheritDoc}
////     */
////    /**
////     * Returns a collection containing the objects in this collection of the given type. Equivalent to calling
////     * {@code withType ( type ) .all ( configureAction )}
////     *
////     * @param type The type of objects to find.
////     * @param configureAction The action to execute for each object in the resulting collection.
////     * @return The matching objects. Returns an empty collection if there are no such objects in this collection.
////     */
////    @Override
////    def <S extends T> DomainObjectCollection<S> withType(Class<S> type, Action<? super S> configureAction) {
////        return null
////    }
/////**
//// * Returns a collection containing the objects in this collection of the given type. Equivalent to calling
//// * {@code withType ( type ) .all ( configureClosure )}.
//// *
//// * @param type The type of objects to find.
//// * @param configureClosure The closure to execute for each object in the resulting collection.
//// * @return The matching objects. Returns an empty collection if there are no such objects in this collection.
//// */
////
////    @Override
////    def <S extends T> DomainObjectCollection<S> withType(Class<S> type, Closure configureClosure) {
////        return null
////    }
////
////    @Override
////    NamedDomainObjectList<ArtifactRepository> matching(Spec<? super ArtifactRepository> spec) {
////        return null
////    }
////
////    /**
////     * {@inheritDoc}
////     */
////    @Override
////    NamedDomainObjectList<ArtifactRepository> matching(Closure spec) {
////        return null
////    }
////
////    /**
////     * Adds an {@code Action} to be executed when an object is added to this collection.
////     *
////     * @param action The action to be executed
////     * @return the supplied action
////     */
////    @Override
////    Action<? super ArtifactRepository> whenObjectAdded(Action<? super ArtifactRepository> action) {
////        return null
////    }
////
////    /**
////     * Adds a closure to be called when an object is added to this collection. The newly added object is passed to the
////     * closure as the parameter.
////     *
////     * @param action The closure to be called
////     */
////    @Override
////    void whenObjectAdded(Closure action) {
////
////    }
////
////    /**
////     * Adds an {@code Action} to be executed when an object is removed from this collection.
////     *
////     * @param action The action to be executed
////     * @return the supplied action
////     */
////    @Override
////    Action<? super ArtifactRepository> whenObjectRemoved(Action<? super ArtifactRepository> action) {
////        return null
////    }
////
////    /**
////     * Adds a closure to be called when an object is removed from this collection. The removed object is passed to the
////     * closure as the parameter.
////     *
////     * @param action The closure to be called
////     */
////    @Override
////    void whenObjectRemoved(Closure action) {
////
////    }
////
////    /**
////     * Executes the given action against all objects in this collection, and any objects subsequently added to this
////     * collection.
////     *
////     * @param action The action to be executed
////     */
////    @Override
////    void all(Action<? super ArtifactRepository> action) {
////
////    }
////
////    /**
////     * Executes the given closure against all objects in this collection, and any objects subsequently added to this collection. The object is passed to the closure as the closure
////     * delegate. Alternatively, it is also passed as a parameter.
////     *
////     * @param action The action to be executed
////     */
////    @Override
////    void all(Closure action) {
////
////    }
////
////    /**
////     * {@inheritDoc}
////     */
////    @Override
////    List<ArtifactRepository> findAll(Closure spec) {
////        return null
////    }
//
//    private ArtifactRepository do_add(ArtifactRepository repo) {
//        repositories+=repo
//        repo
//    }
//
//    private List<ArtifactRepository> repositories = []
//}
