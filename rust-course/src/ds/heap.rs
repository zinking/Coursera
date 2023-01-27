
pub struct Heap<T>
where T: Default,
{
    count: usize,
    items: Vec<T>,
    cmp: fn(&T, &T) -> bool,
}

impl<T> Heap<T>
where T: Default,
{
    pub fn new(cmp: fn(&T, &T) -> bool) -> Self {
        Self {
            count: 0,
            items: vec![T::default()],
            cmp
        }
    }

    pub fn len(&self) -> usize {
        self.count
    }

    pub fn is_empty(&self) -> bool {
        return self.len() == 0;
    }

    fn parent_idx(&self, idx: usize) -> usize {
        idx / 2
    }

    fn left_child_idx(&self, idx: usize) -> usize {
        idx * 2
    }

    fn right_child_idx(&self, idx: usize) -> usize {
        self.left_child_idx(idx) + 1
    }

    fn has_children(&self, idx: usize) -> bool {
        self.left_child_idx(idx) <= self.count
    }

    pub fn add(&mut self, value: T) {
        self.count += 1;
        self.items.push(value);

        let mut idx = self.count;
        while self.parent_idx(idx) > 0 {
            let pdx = self.parent_idx(idx);
            if (self.cmp)(&self.items[idx], &self.items[pdx]) {
                self.items.swap(idx, pdx);
            }
            idx = pdx;
        }
    }

    fn smallest_child_idx(&self, idx: usize) -> usize {
        if self.right_child_idx(idx) > self.count {
            self.left_child_idx(idx)
        } else {
            let ldx = self.left_child_idx(idx);
            let rdx = self.right_child_idx(idx);
            if (self.cmp)(&self.items[ldx], &self.items[rdx]) {
                ldx
            } else {
                rdx
            }
        }
    }
}

impl<T> Heap<T>
where T: Default + Ord,
{
    pub fn new_min() -> Heap<T> {
        Self::new(|a, b| a < b)
    }

    pub fn new_max() -> Heap<T> {
        Self::new(|a, b| a > b)
    }
}

impl<T> Iterator for Heap<T>
where T: Default,
{
    type Item = T;

    fn next(&mut self) -> Option<T> {
        if self.count == 0 {
            return None;
        }

        let next = Some(self.items.swap_remove(1));
        self.count -= 1;

        if self.count > 0 {
            let mut idx = 1;
            while self.has_children(idx) {
                let cdx = self.smallest_child_idx(idx);
                if !(self.cmp)(&self.items[idx], &self.items[cdx]) {
                    self.items.swap(idx, cdx);
                }
                idx = cdx;
            }
        }
        next
    }
}


#[cfg(test)]
mod tests {
    use crate::ds::heap::Heap;

    #[test]
    fn test_empty_heap() {
        let mut heap: Heap<i32> = Heap::new_max();
        assert_eq!(heap.next(), None);
    }

    #[test]
    fn test_min_heap() {
        let mut heap: Heap<i32> = Heap::new_min();
        heap.add(4);
        heap.add(2);
        heap.add(9);
        heap.add(11);
        assert_eq!(heap.len(), 4);
        assert_eq!(heap.is_empty(), false);
        assert_eq!(heap.next(), Some(2));
        assert_eq!(heap.next(), Some(4));
    }
}