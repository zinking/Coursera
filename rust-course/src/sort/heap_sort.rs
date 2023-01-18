

pub fn heap_sort<T: PartialOrd>(arr: &mut [T]) {
    let len = arr.len();
    if len < 2 {
        return;
    }

    for i in (0.. len / 2).rev() {
        heapify(arr, i, len);
    }

    for i in (1..len).rev() {
        arr.swap(0, i);
        heapify(arr, 0, i);
    }
}

pub fn heapify<T: PartialOrd>(arr: &mut [T], root: usize, end: usize) {
    let mut largest = root;
    let left = root * 2 + 1;
    if left < end && arr[left] > arr[largest] {
        largest = left;
    }
    let right = left + 1;
    if right < end && arr[right] > arr[largest] {
        largest = right;
    }
    if largest != root {
        arr.swap(root, largest);
        heapify(arr, largest, end);
    }
}

#[cfg(test)]
mod test {
    use crate::sort::heap_sort::heap_sort;

    #[test]
    fn test_empty_vec() {
        let mut arr: Vec<u8> = vec![];
        heap_sort(&mut arr);
        assert_eq!(arr, vec![])
    }

    #[test]
    fn test_number_vec() {
        let mut arr = vec![5, 3, 1, 7, 9];
        heap_sort(&mut arr);
        assert_eq!(arr, vec![1, 3, 5, 7, 9])
    }

}