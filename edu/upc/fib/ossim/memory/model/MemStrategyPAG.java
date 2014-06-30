package edu.upc.fib.ossim.memory.model;

import java.awt.Color;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import edu.upc.fib.ossim.utils.ColorCell;
import edu.upc.fib.ossim.utils.SoSimException;
import edu.upc.fib.ossim.utils.Translation;


/**
 * Memory Management Strategy implementation for Pagination algorithm
 * 
 * @author Alex Macia
 */
public class MemStrategyPAG extends MemStrategyAdapterNOCONT {
	private int pageSize;

	public MemStrategyPAG(int pageSize) {
		super();
		this.pageSize = pageSize;
	}

	/**
	 * Gets Pagination algorithm information including page size   
	 * 
	 * @return	algorithm information
	 */
	public String getAlgorithmInfo() {
		return Translation.getInstance().getLabel("me_24", pageSize);
	}

	/**
	 * Initializes memory dividing it into frames, its size is determined by page size. 
	 * Creates operating system pages and allocate them.   
	 * 
	 * @param memory		partitions linked list (memory)  
	 * @param strSO			operating system process name
	 * @param size			operating system size	
	 * @param color			operating system background color
	 * @param memory_size	memory size
	 */
	public void initMemory(List<MemPartition> memory, String strSO, int size,  Color color, int memory_size) {

		memory.clear();
		// Create a memory frames, size = page size
		int end = 0;
		while (end <  memory_size) {
			MemPartition b = new MemPartition(end, pageSize);
			memory.add(b);
			end += pageSize;
		}

		//	Add SO.

		ProcessComplete so = new ProcessComplete(0, strSO, size, -1, color);
		int pages;
		if (size%pageSize == 0) pages = size/pageSize;
		else pages = size/pageSize + 1;

		for (int i = 0; i< pages -1 ; i++) {
			ProcessComponent pc = new ProcessPage(so, i, pageSize, true);
			so.addBlock(pc);
		}

		// last page. Variable size
		ProcessComponent pc = new ProcessPage(so, pages -1, size - (pages -1)*pageSize, true);
		so.addBlock(pc);

		allocateSO(memory, null, so, memory_size);


	}
	/**
	 * Initializes virtual memory dividing it into frames, its size is determined by page size. 
	 * Creates operating system pages and allocate them.   
	 * 
	 * @param virtual memory		partitions linked list (virtual memory)  
	 * @param strSO			operating system process name
	 * @param size			operating system size	
	 * @param color			operating system background color
	 * @param memory_size	memory size
	 */
	public void initVirtualMemory(List<MemPartition> virtualmemory, String strSO, int size,  Color color, int memory_size) {

		virtualmemory.clear();
		// Create a memory frames, size = page size
		int end = 0;
		while (end <  5*memory_size) { //size = 5*size of physical memory
			MemPartition b = new MemPartition(end, pageSize);
			virtualmemory.add(b);
			end += pageSize;
		}
//		Add SO.

			ProcessComplete so = new ProcessComplete(0, strSO, size, -1, color);
			int pages;
			if (size%pageSize == 0) pages = size/pageSize;
			else pages = size/pageSize + 1;

			for (int i = 0; i< pages -1 ; i++) {
				ProcessComponent pc = new ProcessPage(so, i, pageSize, true);
				so.addBlock(pc);
			}

			// last page. Variable size
			ProcessComponent pc = new ProcessPage(so, pages -1, size - (pages -1)*pageSize, true);
			so.addBlock(pc);

			allocateSO(virtualmemory, null, so, 5*memory_size);

	}


	/**
	 * Returns false, Pagination algorithm has no external fragmentation
	 * 
	 * @return false
	 */
	public boolean hasExternalFragmentation() {
		return false;
	}

	/**
	 * Returns page number  
	 * 
	 * @param component		process component to get information of
	 * @return	page number
	 */
	public String getProcessComponentInfo(ProcessMemUnit component) {
		return new Integer(((ProcessComponent) component).getBid()).toString();
	}

	/**
	 * Gets process size info. Painter view. 
	 * Number of pages  (eg. "Pages 30 p.)
	 * 
	 * @return size info
	 */
	public String getComponentSizeInfo(ProcessComplete p) {
		return Translation.getInstance().getLabel("me_79") + " " + p.getNumBlocks() + " p.";
	}

	/**
	 * Returns data from a process' pages. For each page: page number, size and if is load into memory  
	 * 
	 * @param process	process to get components data of
	 * @return	data from a process' pages
	 */
	public Vector<Vector<Object>> getProcessComponentsData(ProcessMemUnit process) {
		Vector<Vector<Object>> data = new Vector<Vector<Object>>();

		for (int i = 0; i < ((ProcessComplete) process).getNumBlocks(); i++) {
			ProcessMemUnit block = ((ProcessComplete) process).getBlock(i);

			Vector<Object> row = new Vector<Object>();
			row.add(((ProcessComponent) block).getBid());
			row.add(((ProcessComponent) block).getSize());
			row.add(((ProcessComponent) block).isLoad());

			data.add(row);
		}
		return data;
	}

	/**
	 * Returns memory occupation table header: address, frame, page number, pid, name, process size, duration  
	 * 
	 * @return	memory occupation table header
	 */
	public Vector<Object> getTableHeaderInfo() {
		// Block information table header 
		Vector<Object> header = new Vector<Object>();
		header.add(Translation.getInstance().getLabel("me_35")); // Adress
		header.add(Translation.getInstance().getLabel("me_37")); // Frame
		header.add(Translation.getInstance().getLabel("me_30")); // PID	
		header.add(Translation.getInstance().getLabel("me_38")); // Page
		header.add(Translation.getInstance().getLabel("me_31")); // Name
		header.add(Translation.getInstance().getLabel("me_32")); // Size (Process)
		header.add(Translation.getInstance().getLabel("me_33")); // Duration
		return header;
	}

	/**
	 * Returns a partition occupation data, including process data if partition allocates one.
	 * Cells are ColorCell instance
	 * 
	 * @param m			memory partition
	 * @return 	a partition occupation data
	 * 
	 * @see ProcessMemUnit#getInfo()
	 * @see ColorCell
	 */
	public Vector<Object> getTableBlockInfo(MemPartition m) {
		// Block information table header 
		Vector<Object> info = new Vector<Object>();
		info.add(new ColorCell(new Integer(m.getStart()).toString(), Color.WHITE));
		info.add(new ColorCell(new Integer(m.getStart()/pageSize).toString(), Color.WHITE));
		if (m.getAllocated() != null) info.addAll(m.getAllocated().getInfo());
		else {
			for (int i=2; i<getTableHeaderInfo().size();i++) info.add(new ColorCell("", Color.WHITE));
		}
		return info;
	}

	/**
	 * Returns process form table header. Process' pages table: page number, size (hidden) and if is initially load   
	 * 
	 * @return	process form table header
	 */
	public Vector<Object> getFormTableHeader() {
		Vector<Object> header = new Vector<Object>();
    	header.add(Translation.getInstance().getLabel("me_78"));	// Page
    	header.add("");												// Page size. Hidden
    	header.add(Translation.getInstance().getLabel("me_76"));	// Load?
		return header;
	}


	/**
	 * Returns process form table initial data. Initially a process has one page that would be load into memory   
	 * 
	 * @return 	process form table initial data
	 */
	public Vector<Vector<Object>> getFormTableInitData() {
		Vector<Vector<Object>> data = new Vector<Vector<Object>>();
    	Vector<Object> row = new Vector<Object>();
    	row.add(0);
    	row.add(pageSize);
    	row.add(true);
    	data.add(row);
		return data;
	}


	/**
	 * Returns process allocation tables header, process' pages information: page number, memory frame 
	 * and a valid field that indicates if page is load or it is in the backing store  
	 *  
	 * @return	process allocation tables header      
	 */
	public Vector<Object> getMemProcessTableHeader() {
		Vector<Object> header = new Vector<Object>();
    	header.add(Translation.getInstance().getLabel("me_38"));	// Page
		header.add(Translation.getInstance().getLabel("me_37"));	// Frame
    	header.add(Translation.getInstance().getLabel("me_39"));	// Valid
		return header;
	}

	/**
	 * Returns process allocation tables data, process' pages information: page number, memory frame 
	 * and a valid field that indicates if page is load or it is in the backing store  
	 *  
	 * @param memory	partitions linked list (memory)  
	 * @param p			process to get data of			
	 * 
	 * @return	process allocation tables data
	 */
	public Vector<Vector<Object>> getMemProcessTableData(List<MemPartition> memory, ProcessComplete p) {
    	Vector<Vector<Object>> data = new Vector<Vector<Object>>();
		Vector<Object> page = null;
		Iterator<MemPartition> it;
		MemPartition m;
		int frame = -1;

    	for (int i = 0; i < p.getNumBlocks(); i++) {
    		ProcessComponent child = (ProcessComponent) p.getBlock(i);
    		page = new Vector<Object>();
    		frame = -1;
    		if (child.isLoad()) {
    			// Search frame
    			it = memory.iterator();
    			while (it.hasNext() && frame < 0) {
    				m = it.next();
    				if (m.getAllocated() != null && m.getAllocated().equals(child)) frame = m.getStart()/pageSize;
    			}
    		}
    		page.add(new ColorCell(new Integer(child.getBid()).toString(), Color.WHITE)); // Page number
    		if (frame < 0) {
    			page.add(new ColorCell("", Color.WHITE)); // Frame.
    			page.add(new ColorCell("i", Color.WHITE)); // Invalid
    		} else {
    			page.add(new ColorCell(new Integer(frame).toString(), Color.WHITE)); // Frame
    			page.add(new ColorCell("v", Color.WHITE)); // Valid
    		}
    		data.add(page);
    	}

		return data;
	}

	/**
	 * Adds pages to a process. 
	 * 
	 * @param p		process
	 * @param d		pages data 
	 */
	public void addProcessComponents(ProcessComplete p,  Vector<Vector<Object>> d) {
		for (int i = 0; i< d.size() -1 ; i++) {
			Vector<Object> data = d.elementAt(i);
			ProcessComponent pc = new ProcessPage(p,(Integer) data.elementAt(0), pageSize, (Boolean) data.elementAt(2));
			p.addBlock(pc);
		}

		// last page. Variable size
		int page = d.size() - 1;
		int size = p.getSize() - page*pageSize;
		ProcessComponent pc = new ProcessPage(p,page, size, (Boolean) d.elementAt(page).elementAt(2));
		p.addBlock(pc);
	}

	/**
	 * Removes all process' pages from memory
	 * 
	 * @param memory	partitions linked list (memory)  	
	 * @param b			memory partition containing a process page 
	 */
	public void removeProcessInMemory(List<MemPartition> memory, MemPartition b) {
		// Remove all program blocks from memory
    	int id = b.getAllocated().getPid(); 
    	Iterator<MemPartition> it = memory.iterator();
    	
    	while (it.hasNext()) {
    		MemPartition block = it.next();
    		if (block.getAllocated() != null && block.getAllocated().getPid() == id) block.setAllocated(null);
    	}
	}

	/**
	 * No compaction is needed in this strategy
	 * 
	 */
	public void compaction(List<MemPartition> memory, int memory_size) {
		// Do nothing
	}

	/**
	 * Allocates all process' pages into memory frames, 
	 * some of them may be initially into backing store (not loaded),  
	 * 
	 * @param memory		partitions linked list (memory)  
	 * @param swap			processes into backing store linked list (swap)  
	 * @param allocate		process to allocate
	 * @param memory_size	memory size
	 * 
	 * @throws SoSimException	all process' pages can not be allocated
	 */
	public void allocateProcess(List<MemPartition> memory, List<ProcessMemUnit> swap, ProcessMemUnit allocate, int memory_size) 
			throws SoSimException {
		}
	/**
	 * Allocates a process into memory, in pagination, instead of loading all the pages of a process at a time, 
	 * load the process according to its page orders
	 * 
	 * @param memory		partitions linked list (memory)  
	 * @param swap			processes into backing store linked list (swap)  
	 * @param allocate		process to allocate
	 * @param memory_size	memory size
	 */
	public void allocateQuantumProcess(List<MemPartition> memory, List<ProcessMemUnit> swap, 
			ProcessMemUnit allocate, int memory_size) throws SoSimException {
		Object[] memOrdered = memory.toArray();
		int OSSize = memory.get(0).getAllocated().getParent().getNumBlocks();	
    	Arrays.sort(memOrdered);
    	ProcessComplete parent = allocate.getParent();
    	Map<Integer, List<ProcessComponent>>quantumBlocks = parent.getQuantumBlocks();
    	int key = parent.getUpdatedKey();
    	List<ProcessComponent> values = quantumBlocks.get(key);
    	ProcessMemUnit child;
    	List<MemPartition> candidates = new LinkedList<MemPartition>();
    	// Checking memory frames
    	for (int j = 0; j < values.size(); j++) {
    		child = values.get(j);
    		((ProcessComponent) child).setTime(0);
    		addOtherTime(memory,child);
    		boolean inMemory = isInMemory(memory,child);
    		if(inMemory==false&&swap.contains(child)) this.swapInProcessComponent(memory, swap, child, memory_size);
    		else{
    		if (((ProcessComponent) child).isLoad()&&inMemory ==false) { // Should be allocated   			
        		int i = 0;	
        		MemPartition candidate = null;
        		while (i<memOrdered.length && candidate == null) {
            		MemPartition partition = (MemPartition) memOrdered[i];
            		if (!candidates.contains(partition) && partition.getAllocated() == null) {
            			candidate = partition;// First candidate
            		}
            		i++;
        		}
        		if (candidate != null) candidates.add(candidate);
        		//else throw new SoSimException("me_08");   
        		else {
        			int position = LRUFindPosition(memory,OSSize);//int position = LRUGetPosition();
        			this.swapOutProcess(memory, swap,(MemPartition) memOrdered[position]);	
					candidate = (MemPartition) memOrdered[position];
					candidates.add(candidate);
				}
        		candidate.setAllocated(child);
    		} 
    		
    	}
    	}
    	
    	
	}

	/**
	 * The LRU algorithm to find the position to be replaced
	 * 
	 * @param memory		partitions linked list (memory)  
	 * @param OSSize	OS size
	 */
	private int LRUFindPosition(List<MemPartition> memory,int OSSize) {
		int max = 0;
		int position = OSSize;
		for(int j=OSSize;j<memory.size();j++){
			ProcessComponent p = (ProcessComponent)memory.get(j).getAllocated();	
			if(p!=null&&p.time>max){
				max = p.time;
				position = j;
			}
		}
		return position;
	}
	/**
	 * add one time for all the pages in the physical memory except for the page being used
	 * 
	 * @param memory		partitions linked list (memory)  
	 * @param child
	 */
	private void addOtherTime(List<MemPartition> memory, ProcessMemUnit child) {
		Iterator<MemPartition> it = memory.iterator();
		while(it.hasNext()){
			MemPartition m = it.next();
			ProcessComponent p = (ProcessComponent)m.getAllocated();
			if(p!=null&&!p.equals(child)) p.addTime();
		}
	}
	/**
	 * check if the child being used is in memory or not
	 * 
	 * @param memory		partitions linked list (memory)  
	 * @param child
	 */
	private boolean isInMemory(List<MemPartition> memory, ProcessMemUnit child) {
		Iterator<MemPartition> it = memory.iterator();
		while(it.hasNext()){
			MemPartition m = it.next();
			if(m.getAllocated()!=null&&m.getAllocated().equals(child)) return true;
		}
		return false;
	}

	public void allocateSO(List<MemPartition> memory, List<ProcessMemUnit> swap, ProcessMemUnit allocate, int memory_size) {
		Object[] memOrdered = memory.toArray();
    	Arrays.sort(memOrdered);
    	ProcessComplete parent = allocate.getParent();
    	ProcessMemUnit child;
    	List<MemPartition> candidates = new LinkedList<MemPartition>();
    	
    	// Checking memory frames
    	for (int j = 0; j < parent.getNumBlocks(); j++) {
    		child = parent.getBlock(j);
        	int i = 0;	
        	MemPartition candidate = null;
        	while (i<memOrdered.length && candidate == null) {
            	MemPartition partition = (MemPartition) memOrdered[i];
            	if (!candidates.contains(partition) && partition.getAllocated() == null) {
            		candidate = partition;// First candidate
            	}
            	i++;
        	}
        	 candidates.add(candidate);
        	
    		} 

    	// Allocate pages
    	for (int j = 0; j < parent.getNumBlocks(); j++) {
    		child = parent.getBlock(j);
    		MemPartition block = candidates.remove(0);
    		block.setAllocated(child); 
    	}
	}

	public void allocateVirtualProcess(List<MemPartition> virtualmemory,
			List<ProcessMemUnit> swap, ProcessMemUnit allocate, int memory_size){
		Iterator<MemPartition>it = virtualmemory.iterator();
		while (it.hasNext() ) {
			MemPartition m = it.next();
			if (m.getAllocated() != null && m.getAllocated().getPid()==allocate.getPid()) return;
		}

		Object[] memOrdered = virtualmemory.toArray();
    	Arrays.sort(memOrdered);
    	ProcessComplete parent = allocate.getParent();
    	ProcessMemUnit child;
    	List<MemPartition> candidates = new LinkedList<MemPartition>();
    	
    	// Checking memory frames
    	
    	for (int j = 0; j < parent.getNumBlocks(); j++) {
    		child = parent.getBlock(j);
    		
        		int i = 0;	
        		MemPartition candidate = null;
        		while (i<memOrdered.length && candidate == null) {
            		MemPartition partition = (MemPartition) memOrdered[i];
            		if (!candidates.contains(partition) && partition.getAllocated() == null) {
            			candidate = partition;// First candidate
            		}
            		i++;
        		//}
        		if (candidate != null) candidates.add(candidate);
        		
    		}
    	}
    	
    	
    	// Allocate pages
    	for (int j = 0; j < parent.getNumBlocks(); j++) {
    		child = parent.getBlock(j);
    		//if (((ProcessComponent) child).isLoad()) { // Shoul be allocated
    			MemPartition block = candidates.remove(0);
    			block.setAllocated(child); 
    		//} else swap.add(child); // Not loaded
    	}

	}
	/**
     * Allocates swapped process page from backing store into memory 
	 * 
	 * @param memory		partitions linked list (memory)  
	 * @param swap			processes into backing store linked list (swap)  
	 * @param swapped		swapped process to allocate
	 * @param memory_size	memory size
	 * 
	 * @throws SoSimException	process page can not be allocated
	 * 
	 */
	public void swapInProcessComponent(List<MemPartition> memory, List<ProcessMemUnit> swap, ProcessMemUnit swapped, int memory_size) throws SoSimException {
		Object[] memOrdered = memory.toArray();
    	Arrays.sort(memOrdered);
    	int OSSize = memory.get(0).getAllocated().getParent().getNumBlocks();	
    	int i = 0;	
    	MemPartition candidate = null;
		while (i<memOrdered.length && candidate == null) {
    		MemPartition partition = (MemPartition) memOrdered[i];
    		if (partition.getAllocated() == null) {
    			candidate = partition;// First candidate
    		}
    		i++;
		}

		if (candidate != null) {
			candidate.setAllocated(swapped); 
			((ProcessComponent) swapped).setLoad(true);
		}
		//else throw new SoSimException("me_08");
		else {
			int position = LRUFindPosition(memory,OSSize);
			this.swapOutProcess(memory, swap,(MemPartition) memOrdered[position]);
			candidate = (MemPartition) memOrdered[position];
			candidate.setAllocated(swapped); 
			((ProcessComponent) swapped).setLoad(true);
		}
	}

	/**
	 * Returns address translation, given a process logical address gets its corresponding physical address, 
	 * if page is not into memory gives a page fault
	 * 
	 * @param b				memory partition containing process
	 * @param logicalAddr	process logical address, belong to process logical space 
	 * @param memory		partitions linked list (memory)
	 * 
	 * @return	address translation or page fault error. format "@9999"
	 */
	public String getAddTransPhysical(MemPartition b, int logicalAddr, List<MemPartition> memory) {
		// logicalAddr belongs to process logical space

		ProcessComplete p = b.getAllocated().getParent();
		// Page ?
		int page = logicalAddr/pageSize;
		int offset = logicalAddr%pageSize;

		// Find page
		ProcessComponent programPage = p.getBlock(page); 

		// Page not loaded 
		if (!programPage.isLoad()) return Translation.getInstance().getLabel("me_86"); // page fault 

		Iterator<MemPartition> it = memory.iterator();
		MemPartition block = null;
		boolean found = false;
		while (it.hasNext() && !found) {
			block = it.next();
			if (block.getAllocated() != null && block.getAllocated().equals(programPage)) found = true;  
		}	
		if (found) return "@" + new Integer(block.getStart() + offset).toString();
		else return ""; // never
	}

	/**
	 * Adds pages orders to a process.  (Only in pagination)  
	 * 
	 * @param p		process
	 * @param d		page orders data 
	 */
	public void addQuantumListData(ProcessComplete p,  Object d) {
		String data = (String)d;
		p.setQuantumOrders(data);
	}
	/**
	 * Adds quantum numbers to a process.  (Only in pagination)  
	 * 
	 * @param p		process
	 * @param d		page orders data 
	 */
	public void addQuantum(ProcessComplete p,  Object d){
		Integer i = (Integer)d;
		p.setQuantum(i.intValue());

	}
}