
pub fn quick_sort<T: PartialOrd>(arr: &mut [T]) {
    let len = arr.len();
    if len < 2 {
        return;
    }

    quick_sort_range(arr, 0, len - 1);
}

fn quick_sort_range<T: PartialOrd>(arr: &mut [T], l: usize, r: usize) {
    if l < r {
        let p = partition(arr, l, r);
        if p > 0 {
            quick_sort_range(arr, l, p - 1);
        }

        quick_sort_range(arr, p + 1, r);
    }
}

fn partition<T: PartialOrd>(arr: &mut [T], l: usize, r: usize) -> usize {
    let pivot = r;
    // i point to the original next
    let mut i = l;
    for j in l.. r {
        if arr[j] < arr[pivot] {
            i += 1;
            arr.swap(i - 1, j);
        }
    }
    arr.swap(i, pivot);
    i
}

#[cfg(test)]
mod test {
    use crate::sort::quick_sort::quick_sort;

    #[test]
    fn test_empty_vec() {
        let mut arr: Vec<u8> = vec![];
        quick_sort(&mut arr);
        assert_eq!(arr, vec![])
    }

    #[test]
    fn test_number_vec() {
        let mut arr = vec![3, 1, 5, 7];
        quick_sort(&mut arr);
        assert_eq!(arr, vec![1, 3, 5, 7])
    }

    #[test]
    fn test_number_vec2() {
        let mut arr = vec![7, 49, 73, 58, 30, 72, 44, 78, 23, 9];
        quick_sort(&mut arr);
        assert_eq!(arr, vec![7, 9, 23, 30, 44, 49, 58, 72, 73, 78]);
    }

}