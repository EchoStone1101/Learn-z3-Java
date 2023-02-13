(declare-const x (_ BitVec 8))
(declare-const y (_ BitVec 8))

(assert (= ((_ extract 8 8) (bvadd ((_ zero_extend 1) x) ((_ zero_extend 1) y))) #b0))

(check-sat)
(get-model)