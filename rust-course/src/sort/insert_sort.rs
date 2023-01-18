

pub fn insert_sort<T: PartialOrd>(arr: &mut [T]) {
    let len = arr.len();
    if len < 2 {
        return;
    }

    for i in 1..len {
        let mut j = i;
        while j > 0 && arr[j - 1] > arr[j] {
            arr.swap(j, j - 1);
            j -= 1;
        }
    }
}

#[cfg(test)]
mod test {
    use crate::sort::insert_sort::insert_sort;

    #[test]
    fn test_empty_vec() {
        let mut arr: Vec<u8> = vec![];
        insert_sort(&mut arr);
        assert_eq!(arr, vec![])
    }

    #[test]
    fn test_number_vec() {
        let mut arr = vec![5, 3, 1, 7, 9];
        insert_sort(&mut arr);
        assert_eq!(arr, vec![1, 3, 5, 7, 9])
    }

}
